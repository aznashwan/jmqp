package main

import (
	"flag"
	"fmt"
	"os"
)

// various module-global constants:
var (
	// name respresents the string name of the desired address/topic:
	name string

	// send and read are flags to indicate whether to send
	// or to read the message:
	send, read bool

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

// main is the main entry point of the program:
func main() {
	// parse and check the command line args:
	initFlags()
	flag.Parse()

	if err := validateParams(); err != nil {
		fmt.Println(err)
		os.Exit(-1)
	}

	fmt.Println("COMING SOON")
}
