
function httpGet(theUrl) {
  var xmlHttp = new XMLHttpRequest();
  xmlHttp.open("GET", theUrl, false); // false for synchronous request
  xmlHttp.setRequestHeader("Accept", "application/json");
  xmlHttp.send(null);
  return xmlHttp.responseText;
}


function newJoke() {
	document.getElementById('jokeButton').disabled = true;
	// Get response from Java lambda function
	var numWords = Math.floor(Math.random() * 21) + 10; // Pick random length between 20-30
	var joke = httpGet("https://3ny68iuyxg.execute-api.us-west-1.amazonaws.com/demo/DadJokeGeneratorFunction?numWords=" + numWords);
	var json = JSON.parse(joke);
	console.log(json);
	document.getElementById('joke').innerHTML = json.joke;
	document.getElementById('jokeButton').disabled = false;
}
