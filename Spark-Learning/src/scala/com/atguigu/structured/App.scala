package com.atguigu.structured

import net.sf.json.JSONObject

object App {
  def main(args: Array[String]): Unit = {

    val line1 = "{\"uid\":{\"params\":[{key:\"1\"},{\"current_spm\":\"2\"}]}}"
    val json = JSONObject.fromObject(line1)
    println(json)
  }
}
