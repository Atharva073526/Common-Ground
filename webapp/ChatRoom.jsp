<%
int eventId = Integer.parseInt(String.valueOf(request.getAttribute("eventId")));String eventName = (String) request.getAttribute("eventName");
String eventCity = (String) request.getAttribute("eventCity");
String eventDate = (String) request.getAttribute("eventDate");
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Event Discussion</title>

<style>
body {
    font-family: Arial, sans-serif;
    background: #f0f2f5;
    margin: 0;
}

/* Container */
.chat-container {
    width: 80%;
    margin: 20px auto;
    background: white;
    border-radius: 12px;
    display: flex;
    flex-direction: column;
    height: 90vh;
}

/* Header (Event Info) */
.chat-header {
    padding: 15px;
    border-bottom: 1px solid #ddd;
    display: flex;
    align-items: center;
    gap: 10px;
}

.event-icon {
    width: 45px;
    height: 45px;
    border-radius: 50%;
    background: #ccc;
}

.event-details h3 {
    margin: 0;
}

.event-details p {
    margin: 2px 0;
    font-size: 12px;
    color: gray;
}

/* Chat Area */
.chat-box {
    flex: 1;
    padding: 15px;
    overflow-y: auto;
    background: #f7f7f7;
}

/* Messages */
.msg {
    max-width: 60%;
    padding: 10px;
    margin: 10px 0;
    border-radius: 10px;
    position: relative;
}

.left {
    background: #e0e0e0;
}

.right {
    background: #001f3f;
    color: white;
    margin-left: auto;
}

.time {
    font-size: 10px;
    display: block;
    margin-top: 5px;
}

/* Input Area */
.chat-input {
    display: flex;
    padding: 10px;
    border-top: 1px solid #ddd;
}

.chat-input form {
    display: flex;
    flex: 1;
    gap: 10px; /* creates space between bar and button */
}

.chat-input input {
    flex: 1;
    padding: 10px;
    border-radius: 20px;
    border: 1px solid #ccc;
    outline: none;
}

.chat-input button {
    margin-left: 10px;
    padding: 10px 15px;
    border: none;
    border-radius: 50%;
    background: #001f3f;
    color: white;
    cursor: pointer;
}

.role {
    margin-left: 6px;
    padding: 2px 8px;
    font-size: 11px;
    border-radius: 10px;
    font-weight: 500;
}

/* Soft pastel styles */
.role.coach {
    background: rgba(147, 51, 234, 0.12);
    color: #7c3aed;
}

.role.player {
    background: rgba(37, 99, 235, 0.12);
    color: #2563eb;
}

.role.beginner {
    background: rgba(22, 163, 74, 0.12);
    color: #16a34a;
}

/* fallback */
.role {
    background: rgba(0,0,0,0.08);
    color: #555;
}


</style>

</head>

<body>

<div class="chat-container">

    <!-- 🔹 EVENT HEADER -->
    <div class="chat-header">
        <div class="event-icon"></div>
        <div class="event-details">
            <div class="event-details">
                <h3><%= eventName %></h3>
                <p><%= eventCity %> -- <%= eventDate %></p>
            </div>
        </div>
    </div>

    <!-- 🔹 CHAT MESSAGES -->
    <div class="chat-box" id="chatBox">

        <div class="msg left">


        </div>

        <div class="msg right">

        </div>

    </div>

    <!-- 🔹 INPUT -->
    <div class="chat-input">
        <form id="chatForm">
            <input type="hidden" id="eventId" value="<%= eventId %>">
            <input type="text" id="messageInput" placeholder="Type a message..." required>

            <button type="button" onclick="handleChatSubmit()"> >> </button>
        </form>
    </div>

</div>

<script>
function handleChatSubmit() {
    const eventIdValue = document.getElementById("eventId").value;
    const inputField = document.getElementById("messageInput");
    const message = inputField.value.trim();

    if (message === "") return;

    const params = new URLSearchParams();
    params.append("eventId", eventIdValue);
    params.append("messageInput", message);

    fetch("AddComment", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: params
    })
    .then(response => {
        if (response.ok) {
            inputField.value = "";
            loadMessages(); // Refresh UI immediately after sending
        } else {
            console.error("Server returned an error.");
        }
    })
    .catch(err => console.error("Request failed:", err));
}

function loadMessages() {
    const eventId = document.getElementById("eventId").value;

    fetch(`GetMessages?eventId=${eventId}`)
        .then(response => response.json())
        .then(data => {
            const chatBox = document.getElementById("chatBox");
            chatBox.innerHTML = "";

            <%
            String currentUser = (String) session.getAttribute("userFname");
            if (currentUser == null) currentUser = "Guest";
            %>
            const currentUser = "<%= currentUser %>";
            data.forEach(msg => {
                const div = document.createElement("div");
                const side = (msg.sender === currentUser) ? "right" : "left";

                div.className = `msg ${side}`;
                // Added the msg.time inside a span
                div.innerHTML = `
                    <strong>
                      ${msg.sender}
                      <span class="role ${msg.role.toLowerCase()}">${msg.role}</span>
                    </strong>

                    <div>${msg.text}</div>
                    <span class="time">${msg.time}</span>
                `;
                chatBox.appendChild(div);
            });

            chatBox.scrollTop = chatBox.scrollHeight;
        })
        .catch(err => console.error("Error loading messages:", err));
}

// Initialize
loadMessages();
setInterval(loadMessages, 3000);
</script>

</body>
</html>