package main

import (
	"bufio"
	"container/heap"
	"flag"
	"fmt"
	"io/ioutil"
	"os"
	"path"
	"regexp"
	"sort"
	"strings"
)

type Count struct {
	word  string
	count int
}

type PriorityQueue struct {
	cnts []*Count
	sort.IntSlice
}

func (pq PriorityQueue) Len() int {
	return len(pq.cnts)
}

func (pq PriorityQueue) Less(i, j int) bool {
	return pq.cnts[i].count < pq.cnts[j].count
}

func (pq PriorityQueue) Peek() interface{} {
	return pq.cnts[0]
}

func (pq PriorityQueue) Swap(i, j int) {
	pq.cnts[i], pq.cnts[j] = pq.cnts[j], pq.cnts[i]
}

func (pq *PriorityQueue) Push(x interface{}) {
	pq.cnts = append(pq.cnts, x.(*Count))
}

func (pq *PriorityQueue) Pop() interface{} {
	old := pq.cnts
	n := len(old)
	item := old[n-1]
	old[n-1] = nil // avoid memory leak
	pq.cnts = old[0 : n-1]
	return item
}

const (
	stop_file  = "stop_words.txt"
	words_file = "pride-and-prejudice.txt"
)

var set map[string]bool
var freq map[string]int
var AppPath string

func main() {
	flag.StringVar(&AppPath, "path", "path", "path")
	flag.Parse()
	fmt.Printf("App path: %s\n", AppPath)

	set = make(map[string]bool)
	freq = make(map[string]int)
	initStopMap()
	buildFrequency()

	pq := &PriorityQueue{}
	heap.Init(pq)
	findBiggest(25, pq)

	sort.Sort(pq)
	//for _, item := range pq.cnts {
	//	fmt.Printf("%s %d\n", item.word, item.count)
	//}
	f, _ := os.Create(words_file)
	defer f.Close()
	for i := 0; i < 25; i++ {
		content := fmt.Sprintf("%s  -  %d\n", pq.cnts[24-i].word, pq.cnts[24-i].count)
		f.Write([]byte(content))
	}
}

func findBiggest(num int, pq *PriorityQueue) {
	for key, val := range freq {
		pair := &Count{
			count: val,
			word:  key,
		}
		if pq.Len() < num {
			heap.Push(pq, pair)
		} else {
			item := pq.Peek()
			if val > item.(*Count).count {
				heap.Pop(pq)
				heap.Push(pq, pair)
			}
		}
	}
}

func buildFrequency() {
	file, _ := ioutil.ReadFile(AppPath)

	reg := regexp.MustCompile("[a-z]{2,}")
	words := reg.FindAllString(strings.ToLower(string(file)), -1)

	for _, word := range words {
		if !set[word] {
			freq[word]++
		}
	}
}

func initStopMap() {
	file, err := os.Open(path.Join("../", stop_file))
	if err != nil {
		fmt.Println(err)
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	scanner.Scan()
	content := scanner.Text()

	arr := strings.Split(content, ",")

	for _, val := range arr {
		//fmt.Printf("Stop %s\n", val)
		set[val] = true
	}
}
