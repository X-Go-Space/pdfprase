package router

import (
	"github.com/gin-gonic/gin"
	"pdfprase/controller"
)
func Run(){
	r:=gin.Default()
	r.GET("/",controller.HelloPdfPrase)
	r.GET("/getdata",controller.GetTableData)
	r.Run(":8080")
}