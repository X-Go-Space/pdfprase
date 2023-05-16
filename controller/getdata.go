package controller

import (
	"github.com/gin-gonic/gin"
	"github.com/jiahao-victory/pdfprase/service"
)

func HelloPdfPrase(c *gin.Context){
	c.String(200,"hello,PdfPrase!")
}

func GetTableData(c *gin.Context){
	tableName:=c.Query("tableName")
	endData:=service.TableData(tableName)
	c.String(200,endData)
}
