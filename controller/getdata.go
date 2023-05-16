package controller

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"pdfprase/util"
	"strconv"
)

func HelloPdfPrase(c *gin.Context){
	c.String(200,"hello,PdfPrase!")
}

func GetTableData(c *gin.Context){
	tableName:=c.Query("tableName")
	var results []map[string]interface{}
	rows, err := util.DB.Table(tableName).Select("*").Rows()
	if err != nil {
		fmt.Println(err)
		return
	}
	defer rows.Close()
	columns, err := rows.Columns()
	if err != nil {
		fmt.Println(err)
		return
	}
	for rows.Next() {
		values := make([]interface{}, len(columns))
		valuePtrs := make([]interface{}, len(columns))
		for i := range columns {
			valuePtrs[i] = &values[i]
		}
		if err := rows.Scan(valuePtrs...); err != nil {

		}
		m := make(map[string]interface{})
		for i, col := range columns {
			val := values[i]
			if b, ok := val.([]byte); ok {
				m[col] = string(b)
			} else {
				m[col] = val
			}
		}
		results = append(results, m)
	}
	endData:=""
	for i:=0;i<len(results);i++{
		for _,v:=range results[i]{
			if intvalue,ok:=v.(int);ok{
				endData+=strconv.Itoa(intvalue)
				endData+="    "
			}
			if stringValue,ok:=v.(string);ok{
				endData+=stringValue
				endData+="    "
			}
		}
		endData+="\n"
	}
	c.String(200,endData)
}
