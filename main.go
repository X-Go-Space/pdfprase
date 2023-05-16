package main

func main() {
	/**
	 * testapp
	    err:=util.InitDb("user","psw","testdb")
	    if err!=nil{
		    fmt.Println(err)
	  		return
	    }
	    var openApi string
		flag.StringVar(&openApi,"openapi","close","isopenapi?")
		flag.Parse()
		if openApi=="open"{
			fmt.Println("请等pdf解析完毕，api接口自动开放")
			run.Run()
			router.Run()
			fmt.Println("pdf解析完毕，api接口开放，请访问本地:8080")
		}else{
			run.Run()
	    }
	 */
}
