package run

import (
	"fmt"
	"os/exec"
	"pdfprase/dao"
	"sync"
	"time"
)

func Run() {
	cmd := exec.Command("java", "-jar", ".\\pdfCreateDir.jar")
	out, err := cmd.Output()
	if err != nil {
		fmt.Println(err)
	}
	fmt.Println(string(out))
	wg := sync.WaitGroup{}
	wg.Add(1)
	go func() {
		cmd = exec.Command("java", "-jar", "-Xms8g", "-Xmn8g", "-Xmx10g", ".\\pdfSplit.jar")
		out, err = cmd.Output()
		if err != nil {
			fmt.Println(err)
		}
		fmt.Println(string(out))
		wg.Done()
	}()

	wg.Add(1)
	go func() {
		cmd = exec.Command("java", "-jar", "-Xms4g", "-Xmn3g", "-Xmx5g", "-Dfile.encoding=utf-8", ".\\pdfGetTextFromTika.jar")
		out, err = cmd.Output()
		if err != nil {
			fmt.Println(err)
		}
		fmt.Println(string(out))
		wg.Done()
	}()

	wg.Add(1)
	go func() {
		cmd = exec.Command("java", "-jar", "-Xms6g", "-Xmn6g", "-Xmx8g", ".\\pdfGetImag.jar")
		out, err = cmd.Output()
		if err != nil {
			fmt.Println(err)
		}
		fmt.Println(string(out))
		wg.Done()
	}()


	wg.Add(1)
	time.Sleep(time.Second)
	go func() {
		err = dao.RunTxt("./output")
		if err != nil {
			fmt.Println(err.Error())
		}
		wg.Done()
	}()

	time.Sleep(time.Second)
	wg.Add(1)
	go func() {
		err = dao.RunImag("./output")
		if err != nil {
			fmt.Println(err.Error())
		}
		wg.Done()
	}()

	wg.Add(1)
	go func() {
		cmd = exec.Command("java", "-jar", "-Xms6g", "-Xmn6g", "-Xmx8g", "-Dfile.encoding=utf-8",".\\xml2json.jar")
		out, err = cmd.Output()
		if err != nil {
			fmt.Println(err)
		}
		fmt.Println(string(out))
		dao.RunXml()
		wg.Done()
	}()
	wg.Wait()
}
