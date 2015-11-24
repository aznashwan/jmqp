package main

import (
	"flag"
	"fmt"
	"net/http"
	"os"
	"path/filepath"
	"strings"
)

// various module-global constants:
var (
	// name respresents the string name of the desired address/topic:
	name string

	// send and read are flags to indicate whether to send
	// or to read the message:
	send, read bool

	// message is the string message to be sent.
	message string

	// personal and topic are flags to indicate whether the action
	// should e taken for a personal or topic-oriented interaction:
	personal, topic bool

	// ip is the string representing the IP address of the server:
	ip string

	// port is the integer port number on the server:
	port int

	// timeout is the integer value of the timeout number of seconds:
	timeout int

	// appPath is the string path of the application:
	appPath string
)

// initFlags is a helper function which initialises all the flags.
func initFlags() {
	flag.StringVar(&name, "name", "", "name of the desired address or topic.")
	flag.StringVar(&ip, "ip", "", "ip of the server.")
	flag.StringVar(&appPath, "app-path", "", "application path of the server.")
	flag.StringVar(&message, "message", "", "message to be sent to the server.")
	flag.BoolVar(&send, "send", false, "Indicates send is desired.")
	flag.BoolVar(&read, "read", false, "Indicates read is desired.")
	flag.BoolVar(&personal, "personal", false, "Indicates a person in desired.")
	flag.BoolVar(&topic, "topic", false, "Indicates a topic is desired.")
	flag.IntVar(&port, "port", 0, "The port on the server.")
	flag.IntVar(&timeout, "timeout", 0, "The timeout of the topic message.")
}

// validateParams is a helper function which validates
// the set of parameters given to the client.
func validateParams() error {
	// first; check for the standard mandatories:
	mandatoryParams := map[string]string{
		"name":    name,
		"ip":      ip,
		"appPath": appPath,
	}
	for nam, val := range mandatoryParams {
		if val == "" {
			return fmt.Errorf("Mandatory parameter '%s' was not provided.", nam)
		}
	}

	// then; check that an action flag was provided:
	if !(send || read) && send && read {
		return fmt.Errorf("One and only one of 'send' or 'read' must be provided.")
	}

	// if send, message must be given; else not...
	if send {
		if message == "" {
			return fmt.Errorf("The 'message' parameter must be provided when 'send' is set.")
		}
	} else {
		if message != "" {
			return fmt.Errorf("The 'message' parameter must only be provided when 'send' is set.")
		}
	}

	// after; check for the personal or topic flags:
	if !(personal || topic) && personal && topic {
		return fmt.Errorf("One or only one of 'personal' or 'topic' must be set.")
	}

	// check for port:
	if port == 0 {
		return fmt.Errorf("A value for 'port' must be provided.")
	}

	// check for timeout:
	if timeout != 0 {
		// ensure it's a topic message:
		if !topic {
			return fmt.Errorf("The 'timeout' parameter must only be given is 'topic' is set.")
		}
	} else {
		// ensure it wans't required:
		if topic {
			return fmt.Errorf("The 'timeout' paramters is required if 'topic' is so.")
		}
	}

	return nil
}

// getAppUrl is a helper function which returns the URL of the application:
func getAppUrl() string {
	return "http://" + filepath.Join(fmt.Sprintf("%s:%d", ip, port), appPath)
}

// getRequestType is a helper method which returns the string type of the request:
func getRequestType() string {
	if topic {
		return "Topic"
	}

	return "Personal"
}

// showResponse is a helper method which prints out the details of
// a given http.Response:
func showResponse(resp *http.Response) error {
	body := []byte{}
	n, err := resp.Body.Read(body)
	if err != nil {
		return err
	}
	body = body[:n]

	fmt.Println("Response code: ", resp.StatusCode)
	fmt.Println("Reponse body:\n", string(body))

	return nil
}

// readMessage reads a message from the server using the provided global params:
func readMessage() error {
	client := &http.Client{}

	// prepare the request:
	req, err := http.NewRequest("GET", getAppUrl(), strings.NewReader(name))
	if err != nil {
		return err
	}

	// set the required headers:
	req.Header.Set("Type", getRequestType())

	// then; do the actual request:
	resp, err := client.Do(req)
	if err != nil {
		return err
	}

	return showResponse(resp)
}

// sendMessage sends a message to the server using the provided global params:
func sendMessage() error {

	client := &http.Client{}

	// prepare the request:
	req, err := http.NewRequest("POST", getAppUrl(), strings.NewReader(message))
	if err != nil {
		return err
	}

	// set the required headers:
	req.Header.Set("Type", getRequestType())
	req.Header.Set("To", name)
	if topic {
		req.Header.Set("Timeout", string(timeout))
	}

	// then; do the actual request:
	resp, err := client.Do(req)
	if err != nil {
		return err
	}

	return showResponse(resp)
}

// main is the main entry point of the program:
func main() {
	// parse and check the command line args:
	initFlags()
	flag.Parse()

	if err := validateParams(); err != nil {
		fmt.Println(err)
		os.Exit(-1)
	}

	// now go ahead and do the actual request:
	var err error
	if send {
		err = sendMessage()
	} else {
		err = readMessage()
	}

	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	os.Exit(0)
}
