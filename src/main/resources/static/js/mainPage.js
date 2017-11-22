var stompClient = null;

function websocketConnect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body));
        });
    });
}

function showGreeting(articlesArr) {
    articlesArr.forEach(function (item, i, arr) {
        
        var newArticle = $("<div class='col-md-8 m-b20'></div>")
            .append("<h4><a href=''" + item.url + "'>" + item.title + "</a></h4>")
            .append("<img class='img-responsive center-block article_image' src='" + item.urlToImage + "'/>")
            .append("<p>" + item.description + "</p>")
            .append("<div><span class='pull-left'>Author: " + item.author + "</span>"
                + "<span class='pull-right'>PublishedAt: " + item.publishedAt + "</span></div>");

        $('#articles_container').append(newArticle);
    });
}

