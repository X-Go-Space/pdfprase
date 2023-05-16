package util

import (
	"github.com/jinzhu/gorm"
	_ "github.com/jinzhu/gorm/dialects/mysql"
)

var DB *gorm.DB
var err error
func InitDB(user string,psw string,db string)error{
	DB, err = gorm.Open("mysql", user+":"+psw+"@/"+db+"?charset=utf8&parseTime=True&loc=Local")
	if err!=nil{
		return err
	}
	return nil
}