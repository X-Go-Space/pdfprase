package dao

import (
	"fmt"
	"io/ioutil"
	"os"
	"github.com/jiahao-victory/pdfprase/util"
	"regexp"
	"strings"
)
/*
  1. 先获取第一级的所有文件，第一级的每一个文件都创建一个表格
*/

/*
	1，一个文件一个文件的处理
*/
var filesList []string//output里面的pdf文件
var tableNameList []string//每个表的名字
func GetPdfList(root string)([]string,error){//获取当前文件夹下面的列表，返回的是路径
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
func GetTableNameList(root string)([]string,error){//获取的是文件夹下面的文件名称，是名字
	files, err := ioutil.ReadDir(root)
	if err != nil {
		return nil,err
	}
	tableNameListTemp:=make([]string,0)
	for _, file := range files {
		tableNameListTemp=append(tableNameListTemp,file.Name()[0:len(file.Name())-3])
	}
	return tableNameListTemp,nil
}
func getPdfTxt(root string)(string,error){//获取切割后的pdf文件内容
	fileTxt, err := ioutil.ReadFile(root)
	if err!=nil{
		return "",err
	}
	return string(fileTxt),nil
}
func TrimRule(r rune)bool{
	if r =='\''{
		return true
	}
	return false
}
func getTrueTxt(txt string)string{
	rs:=[]rune(txt)
	ansBs:=make([]rune,0)
	for i:=0;i<len(rs);i++{
		if rs[i]!='\''{
			ansBs=append(ansBs,rs[i])
		}
	}
	return string(ansBs)
}
func judgeHang(txtHang []rune)bool{
	youkuohao:=len(txtHang)
	konggeindex:=len(txtHang)
	for i:=0;i<len(txtHang);i++{
		if txtHang[i]==')'{
			youkuohao=i
			break
		}
	}
	i:=0
	for ;i<len(txtHang);i++{
		if txtHang[i]==' '&&i>youkuohao{
			konggeindex=i
			break
		}
	}
	if i==len(txtHang){
		return false
	}
	b:=string(txtHang[youkuohao+1:konggeindex])
	if len(b)==0{
		return false
	}
	if konggeindex+2<len(txtHang)&&txtHang[konggeindex+2]=='('{
		return false
	}
	pinyin:=util.CnToPinYin(b)
	if len(pinyin)>=64{
		return false
	}else if len(pinyin)<64{
		return true
	}
	return false
}
func insertDb(txt string,orCreateTable bool,tableName string,structMap map[string]bool)error{//创建表，同时压入一条条数据
	txtSum:=strings.Split(txt,"\n")
	mapTxt:=make(map[string]string)//每个字段对应的值
	if orCreateTable{
		before:=make([]rune,0)//之前的文字
		for i:=0;i<len(txtSum);i++{
			runeTemp:=[]rune(txtSum[i])
			if len(runeTemp)==0{
				continue
			}else if runeTemp[0]!='('{
				before=append(before,runeTemp...)
			}else if runeTemp[0]=='('{
				if !judgeHang(runeTemp){
					before=append(before,runeTemp...)
					continue
				}
				if len(before)==0{
					before=runeTemp
				}else if before[0]!='('{
					before=runeTemp
				}else if before[0]=='('{
					stackRune:=make([]rune,0)
					stackZiduan:=make([]rune,0)
					for j:=0;j<len(before);j++{
						if before[j]==')'{
							stackRune=append(stackRune,before[j])
							for k:=j+1;k<len(before);k++{
								if before[k]==' '{
									break
								}else{
									stackZiduan=append(stackZiduan,before[k])
								}
							}
							break
						}else{
							stackRune=append(stackRune,before[j])
						}
					}
					//fmt.Println(string(stackRune[1:len(stackRune)-1]))
					ziduan:=util.CnToPinYin(string(stackZiduan))
					if len(ziduan)==0&&len(stackZiduan)!=0{
						ziduan=string(stackZiduan)
					}
					if len(ziduan)>0{
						mapTxt[ziduan]=getTrueTxt(string(before[len(stackRune)+len(stackZiduan):]))
						structMap[ziduan]=true
					}
					before=runeTemp
					fmt.Println(ziduan,mapTxt[ziduan])
				}
			}
		}
		if len(before)>0{
			stackRune:=make([]rune,0)
			stackZiduan:=make([]rune,0)
			for j:=0;j<len(before);j++{
				if before[j]==')'{
					stackRune=append(stackRune,before[j])
					for k:=j+1;k<len(before);k++{
						if before[k]==' '{
							break
						}else{
							stackZiduan=append(stackZiduan,before[k])
						}
					}
					break
				}else{
					stackRune=append(stackRune,before[j])
				}
			}
			//fmt.Println(string(stackRune[1:len(stackRune)-1]))
			ziduan:=util.CnToPinYin(string(stackZiduan))
			if len(ziduan)==0&&len(stackZiduan)!=0{
				ziduan=string(stackZiduan)
			}
			if len(ziduan)>0{
				mapTxt[ziduan]=getTrueTxt(string(before[len(stackRune)+len(stackZiduan):]))
				structMap[ziduan]=true
				fmt.Println(ziduan,mapTxt[ziduan])
			}
			//create table tableName(Id INT NOT NULL AUTO_INCREMENT,application text,PRIMARY KEY(Id));
			sqlCreateTable:="CREATE TABLE "+tableName+" ( `ID` INT NOT NULL AUTO_INCREMENT,`PdfName` TEXT ,"
			sqlZiduan:=""
			for k,_:=range mapTxt{
				sqlZiduan+="`"+k+"`"
				sqlZiduan+=" TEXT,"
			}
			sqlZiduan+="PRIMARY KEY(`ID`));"
			sqlCreateTable+=sqlZiduan
			fmt.Println(sqlCreateTable)
			err:= util.DB.Exec(sqlCreateTable).Error
			if err!=nil {
				return err
			}
		}

	}else{
		before:=make([]rune,0)//之前的文字
		for i:=0;i<len(txtSum);i++{
			runeTemp:=[]rune(txtSum[i])
			if len(runeTemp)==0{
				continue
			}else if runeTemp[0]!='('{
				before=append(before,runeTemp...)
			}else if runeTemp[0]=='('{
				if !judgeHang(runeTemp){
					before=append(before,runeTemp...)
					continue
				}
				if len(before)==0{
					before=runeTemp
				}else if before[0]!='('{
					before=runeTemp
				}else if before[0]=='('{
					stackRune:=make([]rune,0)
					stackZiduan:=make([]rune,0)
					for j:=0;j<len(before);j++{
						if before[j]==')'{
							stackRune=append(stackRune,before[j])
							for k:=j+1;k<len(before);k++{
								if before[k]==' '{
									break
								}else{
									stackZiduan=append(stackZiduan,before[k])
								}
							}
							break
						}else{
							stackRune=append(stackRune,before[j])
						}
					}
					//fmt.Println(string(stackRune[1:len(stackRune)-1]))
					ziduan:=util.CnToPinYin(string(stackZiduan))
					if len(ziduan)==0&&len(stackZiduan)!=0{
						ziduan=string(stackZiduan)
					}
					if len(ziduan)>0{
						mapTxt[ziduan]=getTrueTxt(string(before[len(stackRune)+len(stackZiduan):]))
						if structMap[ziduan]==false{
							//更改字段
							sqlAlter:="ALTER TABLE "+tableName+" ADD "+"`"+ziduan+"`"+" TEXT;"
							fmt.Println("1 出现新字段:",sqlAlter)
							err:=util.DB.Exec(sqlAlter).Error
							if err!=nil {
								return err
							}
						}
						structMap[ziduan]=true
					}
					before=runeTemp
					fmt.Println(ziduan,mapTxt[ziduan])
				}
			}
		}
		if len(before)>0{
			stackRune:=make([]rune,0)
			stackZiduan:=make([]rune,0)
			for j:=0;j<len(before);j++{
				if before[j]==')'{
					stackRune=append(stackRune,before[j])
					for k:=j+1;k<len(before);k++{
						if before[k]==' '{
							break
						}else{
							stackZiduan=append(stackZiduan,before[k])
						}
					}
					break
				}else{
					stackRune=append(stackRune,before[j])
				}
			}
			//fmt.Println(string(stackRune[1:len(stackRune)-1]))
			ziduan:=util.CnToPinYin(string(stackZiduan))
			if len(ziduan)==0&&len(stackZiduan)!=0{
				ziduan=string(stackZiduan)
			}
			if len(ziduan)>0{
				mapTxt[ziduan]=getTrueTxt(string(before[len(stackRune)+len(stackZiduan):]))
				if structMap[ziduan]==false{
					sqlAlter:="ALTER TABLE "+tableName+" ADD "+"`"+ziduan+"`"+" TEXT;"
					fmt.Println("2 出现新字段:",sqlAlter)
					err:=util.DB.Exec(sqlAlter).Error
					if err!=nil {
						return err
					}
				}
				structMap[ziduan]=true
			}
			fmt.Println(ziduan,mapTxt[ziduan])//所有数据
		}

	}
	//INSERT INTO TABLENAME(COLUMNS1,CLOUMNS2) VALUES(VALUES1,VALUES2);
	sqlInsert:="INSERT INTO "+tableName
	lieSql:=""
	valSql:=""
	for k,v:=range mapTxt{
		lieSql+="`"+k+"`"+","
		valSql+="'"+v+"'"+","
		fmt.Println("字段：",k)
		fmt.Println("值：",v)
	}
	lieSql=lieSql[:len(lieSql)-1]
	valSql=string([]rune(valSql)[:len([]rune(valSql))-1])
	lieSql="(`PdfName`,"+lieSql+")"
	valSql="('"+tableName+"',"+valSql+");"
	re := regexp.MustCompile(`Evaluation Warning : The document was created with Spire\.PDF for Java\.[^·]*·`)
	valSql=re.ReplaceAllString(valSql,"")
	sqlInsert+=lieSql+" VALUES "+valSql
	fmt.Println(sqlInsert)
	err:=util.DB.Exec(sqlInsert).Error
	if err!=nil {
		return err
	}
	return nil
}
func pressDb(root string,tableName string,structMap map[string]bool,flagFirst bool,beforeString string)(string,error){//将root路径下的txt文件进行格式化，并压入数据库
	fmt.Println(1,root,"开始读取文件")//txt文件路径
	txt, err := getPdfTxt(root)//读取第txt文件
	if err!=nil{
		return "",err
	}
	//在这里处理的是一整个txt文件
	txtSum:=strings.Split(txt,"-----------------------------------------")

	if flagFirst{
		//需要根据第一个txt文件创建表格，并压入数据库
		for i:=0;i<len(txtSum)-1;i++{
			if i==0{
				err = insertDb(txtSum[i], true, tableName, structMap)
				if err != nil {
					return "", err
				}
			}else{
				err = insertDb(txtSum[i], false, tableName, structMap)
				if err != nil {
					return "", err
				}
			}
		}
		return txtSum[len(txtSum)-1],nil
	}else{
		if len(txtSum)==1{
			txtSum[0]=beforeString+txtSum[0]
			err = insertDb(txtSum[0], false, tableName, structMap)
			if err != nil {
				return "", err
			}
			return "",nil
		}
		for i:=0;i<len(txtSum)-1;i++{
			if i==0{
				txtSum[i]=beforeString+txtSum[i]
				//fmt.Println(txtSum[i])
				err = insertDb(txtSum[i], false, tableName, structMap)
				if err != nil {
					return "", err
				}
			}else{
				err = insertDb(txtSum[i], false, tableName, structMap)
				if err != nil {
					return "", err
				}
			}
		}
		return txtSum[len(txtSum)-1],nil
	}
	//fmt.Println(txt, tableName)
	return "",nil
}
func CreateTableAndTxt(root string,tableName string)error{//根据txt文件夹下的所有txt文件进行处理
	root=root+"/text"
	fmt.Println(root,tableName)//打印当前路径
	flagRunTxt:=true
	flagFirst:=true//判断第一个传进去的表格
	structMap:=make(map[string]bool)//判断表的结构是否有变化
	beforeString:=""//上一个文件遗留下来的东西
	for flagRunTxt{
		fileTxtList,err:=GetPdfList(root)//获取文件夹下所有的txt文件路径

		if err!=nil{
			return err
		}
		flagNameList,err:=GetTableNameList(root)
		if err!=nil{
			return err
		}
		for i:=0;i<len(flagNameList);i++{
			//fmt.Println(flagNameList[i])
			if flagNameList[i]=="end."{
				flagRunTxt=false
			}
		}
		if len(fileTxtList)<10{
			continue
		}
		for i:=0;i<len(fileTxtList);i++{//处理每一个txt文件
			beforeString,err = pressDb(fileTxtList[i], tableName,structMap,flagFirst,beforeString)
			if err!=nil{
				return err
			}
			flagFirst=false
			err = os.Remove(fileTxtList[i])
			if err!=nil{
				return err
			}
		}
	}
	fileTxtList,err:=GetPdfList(root)//获取文件夹下所有的txt文件路径
	if len(fileTxtList)>0{
		if err!=nil{
			return err
		}
		for i:=0;i<len(fileTxtList);i++{//处理每一个txt文件
			beforeString,err = pressDb(fileTxtList[i], tableName,structMap,flagFirst,beforeString)
			if err!=nil{
				return err
			}
			flagFirst=false
			err = os.Remove(fileTxtList[i])
			if err!=nil{
				return err
			}
		}
	}
	return nil
}
func RunTxt(root string)error{//处理pdf文件夹下的txt文件
	var err error
	filesList,err=GetPdfList(root)
	if err!=nil{
		return err
	}
	tableNameList,err=GetTableNameList(root)
	if err!=nil{
		return err
	}
	for i:=0;i<len(filesList);i++{
		err=CreateTableAndTxt(filesList[i],tableNameList[i])
		if err!=nil{
			return err
		}
	}
	return nil
}