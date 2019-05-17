# es+hbase

## ES+Hbase对接方案概述
Hbase的索引方案有很多，越来越多的人开始选择ES+Hbase的方案，其实该方案并没有想象中那么完美，ES并发低，同时查询速度相对Hbase也慢很多,那为什么会选择他呢，它的写入比较快，如果一个宽表需要建20个索引，在数据导入时，hbase每秒导入20W，那么ES压力就是每秒400W，solr和hindex都不能解决该问题。

所以对并发高的业务场景，还是使用华为HIndex这种方案，也可以混合使用

 

方案描述
ES+Hbase对接大致有两种方式，需要根据当前的业务场景做相应的选择，

方案1：
如果是对写入数据性能要求高的业务场景，那么一份数据先写到Hbase,然后再写到ES中，两个写入流程独立，这样可以达到性能最大，目前某公安厅使用该方案，每天需要写入数据200亿，6T数据，每个记录建20左右的索引。

缺点：可能存在数据的不一致性。

 

方案2：
这也是目前网上比较流行的方案，使用hbase的协处理监听数据在Hbase中的变动，实时的更新ES中的索引，

缺点是协处理器会影响Hbase的性能

hbase rowkey作为es的查询ID,然后将rowkey的字段拆分存在es，这样查询条件直接定位rowkey,然后到hbase get


## 实例
[Elasticsearch+Hbase实现海量数据秒回查询](https://blog.csdn.net/sdksdk0/article/details/53966430)

[Elasticsearch对Hbase中的数据建索引实现海量数据快速查询](https://blog.csdn.net/m0_37739193/article/details/78029734)
```
 // 创建索引
 @Test
    public void createIndex() throws Exception {
        List<Doc> arrayList = new ArrayList<Doc>();
        File file = new File("C:\\Users\\asus\\Desktop\\doc1.txt");
        List<String> list = FileUtils.readLines(file,"UTF8");
        for(String line : list){
            Doc Doc = new Doc();
            String[] split = line.split("\t");
            System.out.print(split[0]);
            int parseInt = Integer.parseInt(split[0].trim());
            Doc.setId(parseInt);
            Doc.setTitle(split[1]);
            Doc.setAuthor(split[2]);
            Doc.setDescribe(split[3]);
            Doc.setContent(split[3]);
            arrayList.add(Doc);
        }
        HbaseUtils hbaseUtils = new HbaseUtils();
        for (Doc Doc : arrayList) {
            try {
                //把数据插入hbase
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_TITLE, Doc.getTitle());
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_AUTHOR, Doc.getAuthor());
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_DESCRIBE, Doc.getDescribe());
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_CONTENT, Doc.getContent());
                //把数据插入es
                Esutil.addIndex("tfjt","doc", Doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
```

```
    // 查询
	@RequestMapping("/search.do")
	public String serachArticle(Model model,
			@RequestParam(value="keyWords",required = false) String keyWords,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize){
		try {
			keyWords = new String(keyWords.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Map<String,Object> map = new HashMap<String, Object>();
		int count = 0;
		try {
			map = Esutil.search(keyWords,"tfjt","doc",(pageNum-1)*pageSize, pageSize);
			count = Integer.parseInt(((Long) map.get("count")).toString());
		} catch (Exception e) {
			logger.error("查询索引错误!{}",e);
			e.printStackTrace();
		}
		PageUtil<Map<String, Object>> page = new PageUtil<Map<String, Object>>(String.valueOf(pageNum),String.valueOf(pageSize),count);
		List<Map<String, Object>> articleList = (List<Map<String, Object>>)map.get("dataList");
		page.setList(articleList);
		model.addAttribute("total",count);
		model.addAttribute("pageNum",pageNum);
		model.addAttribute("page",page);
		model.addAttribute("kw",keyWords);
		return "index.jsp";
	}
	
	// Esutil.search
	public static Map<String, Object> search(String key,String index,String type,int start,int row){
		SearchRequestBuilder builder = getClient().prepareSearch(index);
		builder.setTypes(type);
		builder.setFrom(start);
		builder.setSize(row);
		//设置高亮字段名称
		builder.addHighlightedField("title");
		builder.addHighlightedField("describe");
		//设置高亮前缀
		builder.setHighlighterPreTags("<font color='red' >");
		//设置高亮后缀
		builder.setHighlighterPostTags("</font>");
		builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		if(StringUtils.isNotBlank(key)){
//			builder.setQuery(QueryBuilders.termQuery("title",key));
			builder.setQuery(QueryBuilders.multiMatchQuery(key, "title","describe"));
		}
		builder.setExplain(true);
		SearchResponse searchResponse = builder.get();
		
		SearchHits hits = searchResponse.getHits();
		long total = hits.getTotalHits();
		Map<String, Object> map = new HashMap<String,Object>();
		SearchHit[] hits2 = hits.getHits();
		map.put("count", total);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (SearchHit searchHit : hits2) {
			Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
			HighlightField highlightField = highlightFields.get("title");
			Map<String, Object> source = searchHit.getSource();
			if(highlightField!=null){
				Text[] fragments = highlightField.fragments();
				String name = "";
				for (Text text : fragments) {
					name+=text;
				}
				source.put("title", name);
			}
			HighlightField highlightField2 = highlightFields.get("describe");
			if(highlightField2!=null){
				Text[] fragments = highlightField2.fragments();
				String describe = "";
				for (Text text : fragments) {
					describe+=text;
				}
				source.put("describe", describe);
			}
			list.add(source);
		}
		map.put("dataList", list);
		return map;
   }
```