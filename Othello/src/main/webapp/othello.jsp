<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.*,javax.servlet.*,java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>オセロゲーム</title>
    <link rel="stylesheet" href="css/style.css"/>
    <script>
        function startCountdown() {
            let count = 3;
            let countdown = setInterval(function() {
                document.getElementById('countdown').innerText = 'AI、考え中・・・残り時間: ' + count + '秒';
                count--;
                if (count < 0) {
                    clearInterval(countdown);
                    window.location.href = '?aiMove=true';
                }
            }, 1000);
        }
    </script>
</head>
<body>
	<div class="wrapper">
    <h1>オセロでGo!</h1>
    <table border='1'>
        <%
            char[][] board = (char[][]) request.getAttribute("board");
            boolean aiMovePending = Boolean.TRUE.equals(request.getAttribute("aiMovePending"));
            boolean gameFinished = Boolean.TRUE.equals(request.getAttribute("gameFinished"));
            Integer playerScoreObj = (Integer) request.getAttribute("playerScore");
            Integer aiScoreObj = (Integer) request.getAttribute("aiScore");

            int playerScore = (playerScoreObj != null) ? playerScoreObj : 0;
            int aiScore = (aiScoreObj != null) ? aiScoreObj : 0;

            for (int i = 0; i < 8; i++) {
                out.println("<tr>");
                for (int j = 0; j < 8; j++) {
                    out.println("<td>");
                    if (board[i][j] == 'W') {
                        out.print("●");
                    } else if (board[i][j] == 'B') {
                        out.print("○");
                    } else {
                        out.print("<a href='?move=" + i + "," + j + "'> - </a>");
                    }
                    out.println("</td>");
                }
                out.println("</tr>");
            }
        %>
    </table>
    
    <% if (aiMovePending) { %>
        <script>
            window.onload = startCountdown;
        </script>
        <p id="countdown">AI考え中・・・残り時間: 3秒</p>
        <img src="images/chara.jpg" width="150px">
    <% } else if (gameFinished) { %>
        <h2>ゲーム終了</h2>
        <img src="images/chara2.jpg" width="150px">
        <p>プレイヤーの得点: <%= playerScore %></p>
        <p>AIの得点: <%= aiScore %></p>
        <% if (playerScore > aiScore) { %>
            <p>プレイヤーの勝利です！</p>
        <% } else if (aiScore > playerScore) { %>
            <p>AIの勝利です！</p>
        <% } else { %>
            <p>引き分けです！</p>
        <% } %>
        <br><a href="?action=reset" class="button">再プレイ</a><a href="index.jsp" class="button">トップに戻る</a>
    <% } else { %>
        <br><br><a href="?action=skip" class="button">スキップ</a>
        <a href="?action=giveup" class="button">ギブアップ</a>
    <% } %><br>
    </div>
</body>
</html>
