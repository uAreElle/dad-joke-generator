# Markov Chain Dad Joke Generator

The program works by storing jokes from the Internet's largest selection of dad jokes, icanhazdadjoke, which are retrieved through GET requests to their search API (https://icanhazdadjoke.com/api). The program trains a markov chain by storing every word in a joke and its directly subsequent word (and its frequency). The markov chain is used to generate a new sequence of strings based on these frequencies.

When the "Give me a dad joke!" button is pressed, a GET request call is made to the AWS API Gateway which triggers the AWS Lambda function containing the program in Java. The lambda function returns a JSON object of the newly generated dad joke which is then displayed on the page.

## How to Run

* Open dadJokeSite.html file in browser
* Click "Give me a dad joke!" button 
* Wait for Markov chain generated dad joke
* Enjoy!

## Future features/improvements

* Improve speed by refactoring program to only train markov chain as needed 
* Improve speed by caching new dad jokes in database
* Spruce up CSS
* And many more...!
	
