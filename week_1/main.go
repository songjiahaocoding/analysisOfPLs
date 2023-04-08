package main

import (
	"bufio"
	"container/heap"
	"fmt"
	"io/ioutil"
	"os"
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
	return pq.cnts[len(pq.cnts)-1]
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
	stop_file  = "./stop_words.txt"
	words_file = "./pride-and-prejudice.txt"
)

var set map[string]bool
var freq map[string]int

func main() {
	set = make(map[string]bool)
	freq = make(map[string]int)
	initStopMap()
	buildFrequency()
	pq := &PriorityQueue{}
	heap.Init(pq)
	for key, val := range freq {
		if val > 200 {
			fmt.Printf(">200: %s\n", key)
		}
		pair := &Count{
			count: val,
			word:  key,
		}
		if pq.Len() < 25 {
			heap.Push(pq, pair)
		} else {
			item := pq.Peek()
			if val > item.(*Count).count {
				heap.Pop(pq)
				heap.Push(pq, pair)
			}
		}
	}

	for _, item := range pq.cnts {
		fmt.Printf("%s %d\n", item.word, item.count)
	}
}

func buildFrequency() {
	file, _ := ioutil.ReadFile(words_file)

	reg := regexp.MustCompile("[a-z]{2,}")
	words := reg.FindAllString(string(file), -1)

	for _, word := range words {
		if !set[word] {
			freq[word]++
		}
	}
}

func initStopMap() {
	file, _ := os.Open(stop_file)
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
