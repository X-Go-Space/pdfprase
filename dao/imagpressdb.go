package dao

import (
	"fmt"
	"pdfprase/util"
)
func CreateTableAndImag(filePdf string,tableName string)error{
	currentTableName:=tableName
	tableName+="imag"
	sqlCreateTable:="CREATE TABLE "+tableName+" ( `ID` INT NOT NULL AUTO_INCREMENT,`Addr` TEXT ,`PdfName` TEXT ,PRIMARY KEY(`ID`));"
	err:= util.DB.Exec(sqlCreateTable).Error
	if err!=nil{
		return err
	}
	fileImagSum:=filePdf+"/imag"
	flagPressImag:=true
	mapFlagPreeImag:=make(map[string]bool)
	for flagPressImag{
		fileImagAddr,err2:=GetPdfList(fileImagSum)
		if err2!=nil{
			return err2
		}
		fileImagName,err2:=GetTableNameList(fileImagSum)
		if err2!=nil{
			return err2
		}
		for i:=0;i<len(fileImagName);i++{
			if fileImagName[i]=="end."{
				flagPressImag=false
			}
		}
		if len(fileImagAddr)<5{
			continue
		}
		for i:=0;i<len(fileImagAddr);i++{
			if !mapFlagPreeImag[fileImagAddr[i]]{
				sqlInsert:="INSERT INTO "+tableName+"(`Addr`,`PdfName`) VALUES "+"('"+fileImagAddr[i]+"','"+currentTableName+"');"
				fmt.Println(sqlInsert)
				err2=util.DB.Exec(sqlInsert).Error
				fmt.Println(sqlInsert)
				if err2!=nil{
					return err2
				}
				mapFlagPreeImag[fileImagAddr[i]]=true
			}
		}
	}

	return err
}
func RunImag(root string)error{
	filePdfList,err:=GetPdfList(root)//获取每个pdf的文件路径
	if err!=nil{
		return err
	}
	fileTableImagList,err:=GetTableNameList(root)//获取要压入的文件的表
	if err!=nil{
		return err
	}
	fmt.Println(filePdfList,fileTableImagList)
	for i:=0;i<len(filePdfList);i++{
		fmt.Println("press imag ",filePdfList[i],fileTableImagList[i])
		err2 := CreateTableAndImag(filePdfList[i], fileTableImagList[i])
		if err2!=nil{
			return err2
		}
	}
	return nil
}