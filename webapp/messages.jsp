<!DOCTYPE html>
<html>
<head>
    <title>Messages</title>
    <link rel="stylesheet" href="msgstyle.css" />
</head>
<body>

<div class="cg-msg-board">
  <div class="cg-msg-header">
    <h2 class="cg-msg-title">Communication Center</h2>

    <div class="cg-tab-navigator">
      <button class="cg-tab-link active" onclick="switchCgTab(event, 'cg-inbox-view')">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
        Inbox
      </button>
      <button class="cg-tab-link" onclick="switchCgTab(event, 'cg-events-view')">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        My Events
      </button>
    </div>
  </div>

  <div id="cg-inbox-view" class="cg-tab-panel active">
    <div class="cg-notice-card">
      <div class="cg-notice-text">
        <span class="cg-user-tag">GPL</span> sent you a request
      </div>
      <div class="cg-action-group">
        <button class="cg-btn-primary">Accept</button>
        <button class="cg-btn-secondary">Reject</button>
      </div>
    </div>
  </div>

  <div id="cg-events-view" class="cg-tab-panel" style="display: none;">
    <div class="cg-notice-card">
      <div class="cg-notice-text">
        <span class="cg-user-tag">Rahul</span> wants to join your event
      </div>
      <div class="cg-action-group">
        <button class="cg-btn-primary">Approve</button>
        <button class="cg-btn-secondary">Decline</button>
      </div>
    </div>
  </div>
</div>

<script>

// Move these functions OUTSIDE so they can be called anytime
function loadInbox() {
    fetch('/new_common/GetRequests')
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById('cg-inbox-view');
            container.innerHTML = '<h3 class="cg-msg-title">Communication Center</h3>';

            if (data.length === 0) {
                container.innerHTML += '<div class="cg-notice-card">No pending requests.</div>';
                return;
            }

            data.forEach(req => {
                const card = document.createElement('div');
                card.className = 'cg-notice-card';
                card.id = `conn-${req.id}`;
                card.innerHTML = `
                    <div class="cg-notice-text">
                        <span class="cg-user-tag">${req.sender}</span> wants to join <strong>${req.event}</strong>
                    </div>
                    <div class="cg-action-group">
                        <button class="cg-btn-primary" onclick="handleAction(${req.id}, 'accepted')">Accept</button>
                        <button class="cg-btn-secondary" onclick="handleAction(${req.id}, 'rejected')">Reject</button>
                    </div>`;
                container.appendChild(card);
            });
        });
}

function loadJoinedEvents() {
    fetch('/new_common/GetJoinedEvents')
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById('cg-events-view');
            container.innerHTML = '<h3 class="cg-msg-title">My Joined Events</h3>';

            if (data.length === 0) {
                container.innerHTML += '<div class="cg-notice-card">You haven\'t joined any events yet.</div>';
                return;
            }

            data.forEach(event => {
                const card = document.createElement('div');
                card.className = 'cg-notice-card';
                card.innerHTML = `
                    <div class="cg-notice-text">
                        Confirmed for <strong>${event.event}</strong>
                        <br><small>Host: ${event.admin}</small>
                    </div>
                    <div class="cg-action-group">
                        <span class="status-badge" style="color: #2e9e6b; font-weight: bold;">Confirmed</span>
                    </div>`;
                container.appendChild(card);
            });
        });
}

function switchCgTab(event, targetId) {
    // 1. UI Switch: Hide all panels
    const panels = document.querySelectorAll('.cg-tab-panel');
    panels.forEach(panel => {
        panel.style.display = 'none';
        panel.classList.remove('active');
    });

    // 2. UI Switch: Deactivate all buttons
    const buttons = document.querySelectorAll('.cg-tab-link');
    buttons.forEach(btn => btn.classList.remove('active'));

    // 3. UI Switch: Show selected panel & highlight button
    const activePanel = document.getElementById(targetId);
    if (activePanel) {
        activePanel.style.display = 'block';
        activePanel.classList.add('active');
    }
    event.currentTarget.classList.add('active');

    // 4. Data Switch: Load content based on tab
    if (targetId === 'cg-inbox-view') {
        loadInbox();
    } else if (targetId === 'cg-events-view') {
        loadJoinedEvents();
    }
}

// Helper for the buttons in the Inbox
function handleAction(connId, status) {
    const params = new URLSearchParams();
    params.append('connId', connId);
    params.append('status', status);

    fetch('/new_common/UpdateStatusServlet', { method: 'POST', body: params })
    .then(res => res.text())
    .then(result => {
        if (result.trim() === "success") {
            const card = document.getElementById(`conn-${connId}`);
            card.style.opacity = '0';
            setTimeout(() => card.remove(), 300);
        }
    });
}

// Initial load
document.addEventListener('DOMContentLoaded', loadInbox);

</script>

</body>
</html>