package util

import (
	"github.com/mozillazg/go-pinyin"
	"strings"
)

func CnToPinYin(cn string)string{
	cnUtil:=pinyin.NewArgs()
	ansPinYin:=strings.Join(pinyin.LazyConvert(cn,&cnUtil),"-")
	return ansPinYin
}
