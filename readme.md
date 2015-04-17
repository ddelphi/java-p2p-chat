
#Readme

##Overview

This is a simple project of P2P chat. Mainly to demonstrate the simple P2P concept, but not cover the distribute network concept (which you should improve yourself via the Google and Wikipedia).

##Example

This is an eclipse project, you can import it into the eclipse, and then navigate to the folder 'example', then run the MainExample class.

The console will show some process info of the AutoClient class, and finally showing two user's talk to each other with the numb words.

There is another example for manual use, which reside in the folder 'client_example'.

To try the manual example, you should follow these instructions:

* run the Main_1 class, then run the Main_2 class (means two users)
* typing commands below in each console:

	:run
	:setServer 127.0.0.1|9088
	:setUser jack
	:getListUser

* then typing words in each console, to say to each other


##Structure

This project uses simple event technique, objects mainly using message to communicate to each other, as well as the client and server. And it uses the cached thread pool technique, to use the multithread efficiently.

There are two block layers in the project, one layer contains global objects, the other one has the main logic objects.

The main structure of the project is:

	* action
	* info

	* verifier
	* messenger
	
	* network

The process of the program is very simple, see the process below:

	# input object make a message,
	  trigger "client_input" event

	# the input action object receive the message,
	  then prepare the final message to be sent via the network,
	  then add to the message container MsgOutBox

	# finally the network object will auto fetch messages from the MsgOutBox,
	  then send to the other end

##Todo

There are so many things to be done, to complete the project, to make it more useful. But there isn't much time for me to do these things. Maybe some day I will pick it up again.
