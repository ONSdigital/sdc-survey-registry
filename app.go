package main

import (
	"fmt"
	"log"
	"net/http"
	"os"
)

func handler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Hi there, I love %s!", r.URL.Path[1:])
}

func main() {

	port := os.Getenv("PORT")
	if port == "" {
		port = "8000"
		log.Print("$PORT set to " + port)
	}

	http.HandleFunc("/", handler)
	http.ListenAndServe(":"+port, nil)
}
