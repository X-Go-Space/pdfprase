
package dao
import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"github.com/jiahao-victory/pdfprase/util"
	"strconv"
	"strings"
)
type Blog struct {
	ID      int
	Author   struct {
		Name  string
		Email string
	}`gorm:"embedded"`
	Upvotes int32
}
var tableName string
var mapStruct map[string]bool

func GetJsonList(root string)([]string,error){//获取当前文件夹下面的列表，返回的是路径
	files, err := ioutil.ReadDir(root)
	if err != nil {
		return nil,err
	}
	filesName:=make([]string,0)
	for _, file := range files {
		if file.Name()=="end.txt"{
			continue
		}
		filesName=append(filesName,root+"/"+file.Name())
	}
	return filesName,nil
}
func getJSON(root string)(string,error){//获取切割后的pdf文件内容
	fileTxt, err := ioutil.ReadFile(root)
	if err!=nil{
		return "",err
	}
	return string(fileTxt),nil
}
func RunXml() {
	fileList,err:=GetJsonList("./xmlresource/jsondir")
	if err!=nil{
		return
	}
	mapStruct=make(map[string]bool)
	tableName="testxml"
	sqlCreateTable:="CREATE TABLE "+tableName+" ( `ID` INT NOT NULL AUTO_INCREMENT,"
	sqlCreateTable+="PRIMARY KEY(`ID`));"
	err= util.DB.Exec(sqlCreateTable).Error
	if err!=nil {
		fmt.Println(err.Error())
		return
	}
	for k:=1;k<len(fileList)+1;k++{
		strdata,err:=getJSON(fileList[k-1])
		if err!=nil {
			return
		}
		jsonStr := []byte(strdata)    //获取json数据将其转换成byte数组
		//jsonStr := []byte(`[{"data":[{"a":"aa","b":null},{"c":[1,2,2]},{"list":["dd","1","ff"]}]}]`)


		sqlInit:="INSERT INTO "+tableName+" (ID) VALUES ( "+strconv.Itoa(k)+" );"
		err= util.DB.Exec(sqlInit).Error
		if err!=nil {
			fmt.Println(err.Error())
			return
		}
		//在这里读取文件
		if (strings.Index(string(jsonStr[:]), "[") == 0) {
			var f []interface{}
			err := json.Unmarshal(jsonStr, &f)
			if err != nil {

			}
			jsonArrayParse(f,"array",k)
		} else {
			var f interface{}
			err := json.Unmarshal(jsonStr, &f)
			if err != nil {

			}
			jsonObjectParse(f,"",k)
		}

	}
}
func jsonArrayParse(vv []interface{},tag string,id int){
	for i, u := range vv {
		switch vv1 := u.(type) {
		case string:
			strData:=tag+strconv.Itoa(i)
			if strings.Contains(strData,"@id"){
				continue
			}
			if strings.Contains(strData,"@num"){
				continue
			}
			if len(vv1)==1{
				continue
			}
			strData=strings.Replace(strData, "business:", "", -1)
			strData=strings.Replace(strData, "PatentDocumentAndRelated", "XML", -1)
			if len(strData)>64{
				strData=strData[len(strData)-64:]
			}
			if len(mapStruct)>=4096{
				fmt.Println("表列太多")
				continue
			}
			if mapStruct[strData]==false{
				sqlAlter:="ALTER TABLE "+tableName+" ADD "+"`"+strData+"`"+" TEXT;"
				err:=util.DB.Exec(sqlAlter).Error
				if err!=nil {

					continue
				}
				mapStruct[strData]=true
			}

			sqlInsert:="UPDATE "+tableName+" SET `"+strData+"` = "+" '"+vv1+"' "+"WHERE ID = "+strconv.Itoa(id)+";"
			fmt.Println(sqlInsert)
			err:=util.DB.Exec(sqlInsert).Error
			if err!=nil {

				continue
			}
			fmt.Println(strData, "------------[string] :", vv1)
		case float64:
			fmt.Println(tag+strconv.Itoa(i), "[float64]:", u)
		case bool:
			fmt.Println(tag+strconv.Itoa(i), "[bool]:", u)
		case nil:
			fmt.Println(tag+strconv.Itoa(i), "[nil]:", u)
		case []interface{}:
			//fmt.Println(i, "[array_] :", u)
			jsonArrayParse(vv1,tag+strconv.Itoa(i),id)
		case interface{}:
			//fmt.Println(i, "[interface_]:",u)
			m1 := u.(map[string]interface{})
			jsonObjectParse(m1,tag+strconv.Itoa(i),id)
		default:
			fmt.Println("  ", tag+strconv.Itoa(i), "[type?]", u, ", ",vv1)
		}
	}
}

func jsonObjectParse(f interface{},tag string,id int){
	m := f.(map[string]interface{})
	for k, v := range m {
		switch vv := v.(type) {
		case string:
			strData:=tag+k
			if strings.Contains(strData,"@id"){
				continue
			}
			if strings.Contains(strData,"@num"){
				continue
			}
			if len(vv)==1{
				continue
			}
			strData=strings.Replace(strData, "business:", "", -1)
			strData=strings.Replace(strData, "PatentDocumentAndRelated", "XML", -1)
			strData=strings.Replace(strData, ":AddressBookbase:Addressbase:", "", -1)
			if len(strData)>64{
				strData=strData[len(strData)-64:]
			}
			if len(mapStruct)>=4096{
				fmt.Println("表列太多")
				continue
			}
			if mapStruct[strData]==false{
				sqlAlter:="ALTER TABLE "+tableName+" ADD "+"`"+strData+"`"+" TEXT;"
				err:=util.DB.Exec(sqlAlter).Error
				if err!=nil {

					continue
				}
				mapStruct[strData]=true
			}
			sqlInsert:="UPDATE "+tableName+" SET `"+strData+"` = "+" '"+vv+"' "+"WHERE ID = "+strconv.Itoa(id)+";"
			fmt.Println(sqlInsert)
			err:=util.DB.Exec(sqlInsert).Error
			if err!=nil {

				continue
			}
			fmt.Println(strData, "[string] :", vv)
		case float64:
			fmt.Println(tag+k, "[float64]:", vv)
		case bool:
			fmt.Println(tag+k, "[bool]:", vv)
		case nil:
			fmt.Println(tag+k, "[nil]:", vv)
		case []interface{}:
			//fmt.Println(k, "[array]:")
			jsonArrayParse(vv,tag+k,id)
		case interface{}:
			//fmt.Println(k, "[interface]:",vv)
			m1 := v.(map[string]interface{})
			jsonObjectParse(m1,tag+k,id)
		default:
			fmt.Println(tag+k, "[type?]",vv)
		}
	}
}
