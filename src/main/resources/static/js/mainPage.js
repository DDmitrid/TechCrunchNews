var stompClient = null;

function websocketConnect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            addNewArticles(JSON.parse(greeting.body));
        });
    });
}

function addNewArticles(articlesArr) {
    articlesArr.forEach(function (item, i, arr) {

        var newArticle = $("<div class='col-md-8 m-b-20'></div>")
            .append("<h4><a id='title' href=''" + item.url + "'>" + item.title + "</a></h4>")
            .append("<img class='img-responsive center-block article_image' src='" + item.urlToImage + "'/>")
            .append("<p id='description'>" + item.description + "</p>")
            .append("<span class='pull-left'>Author:   <span id='publishedAt'>" + item.author + "</span></span>"
                + "<span class='pull-right'>PublishedAt:   <span id='publishedAt'>" + item.publishedAt + "</span></span>");

        // $('#articles_container').append(newArticle);
        $('#articles_container').prepend(newArticle);
    });
}

function sendLastAtricle() {
    var url = $("#article").first().find('#title').attr('href');
    var title = $("#article").first().find('#title').text();
    var urlToImage = $("#article").first().find('img').attr('src');
    var description = $("#article").first().find('#description').text();
    var author = $("#article").first().find('#author').text();
    var publishedAt = $("#article").first().find('#publishedAt').text();
    console.log(new Date(publishedAt));
    console.log(Date.parse(publishedAt));

    var article = {
        "url": url,
        "title": title,
        "urlToImage": urlToImage,
        "description": description,
        "author": author,
        "publishedAt": Date.parse(publishedAt)
    };
    console.log(JSON.stringify(article, undefined, 2));

    stompClient.send("/app/hello", {}, JSON.stringify(article));

}

