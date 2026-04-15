// ===== NAV LINK SWITCHING =====
//const navLinks = document.querySelectorAll('.nav-link');
//
//navLinks.forEach(link => {
//  link.addEventListener('click', function () {
//    navLinks.forEach(l => l.classList.remove('active'));
//    this.classList.add('active');
//  });
//});


// ===== NOTIFICATION BELL =====
const notifBell = document.getElementById('notifBell');

notifBell.addEventListener('click', function () {
  const badge = document.getElementById('notifBadge');
  if (badge) {
    badge.style.display = 'none';
  }
});


// ===== CONNECT BUTTON =====
function handleConnect(btn) {
  if (btn.classList.contains('connected')) {
    btn.classList.remove('connected');
    btn.textContent = 'Connect';
  } else {
    btn.classList.add('connected');
    btn.textContent = 'Connected ✓';
    showToast('Connection request sent!');
  }
}


// ===== FILTER PANEL TOGGLE =====
const filterToggle = document.getElementById('filterToggle');
const filterPanel = document.getElementById('filterPanel');

filterToggle.addEventListener('click', function () {
  const isOpen = filterPanel.classList.toggle('open');
  filterToggle.classList.toggle('open', isOpen);
});


// ===== FILTER VARIABLES =====
let activeLocation = 'all';
let activeLevel = 'all';


// ===== FILTER CLICK =====
const chips = document.querySelectorAll('.chip');

chips.forEach(chip => {
  chip.addEventListener('click', function () {

    const filterType = this.dataset.filter;
    const value = this.dataset.value;

    document.querySelectorAll(`.chip[data-filter="${filterType}"]`)
        .forEach(c => c.classList.remove('active'));

    this.classList.add('active');

    if (filterType === 'location') {
      activeLocation = value;
    }

    if (filterType === 'level') {
      activeLevel = value;
    }

    applyFilters();
    updateFilterCount();
  });
});


// ===== APPLY FILTERS =====
function applyFilters() {

  const cards = document.querySelectorAll('#matchList .user-card');
  let visibleCount = 0;

  cards.forEach(card => {

    const km = parseFloat(card.dataset.km);
    const level = card.dataset.level;

    const locationOk = activeLocation === 'all' || km <= parseFloat(activeLocation);
    const levelOk = activeLevel === 'all' || level === activeLevel;

    if (locationOk && levelOk) {
      card.style.display = '';
      visibleCount++;
    } else {
      card.style.display = 'none';
    }

  });

  const emptyState = document.getElementById('emptyState');

  if (visibleCount === 0) {
    emptyState.style.display = 'flex';
  } else {
    emptyState.style.display = 'none';
  }
}


// ===== FILTER COUNT =====
function updateFilterCount() {

  const countEl = document.getElementById('filterCount');
  let count = 0;

  if (activeLocation !== 'all') count++;
  if (activeLevel !== 'all') count++;

  if (count > 0) {
    countEl.textContent = count;
    countEl.style.display = 'inline-flex';
  } else {
    countEl.style.display = 'none';
  }
}


// ===== CLEAR FILTERS =====
const clearBtn = document.getElementById('clearFilters');

clearBtn.addEventListener('click', function () {

  activeLocation = 'all';
  activeLevel = 'all';

  document.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));

  document.querySelector('.chip[data-filter="location"][data-value="all"]').classList.add('active');
  document.querySelector('.chip[data-filter="level"][data-value="all"]').classList.add('active');

  applyFilters();
  updateFilterCount();
});


// ===== MODAL =====
const modalOverlay = document.getElementById('modalOverlay');
const openModalBtn = document.getElementById('openModal');
const closeModalBtn = document.getElementById('closeModal');
const cancelModalBtn = document.getElementById('cancelModal');

openModalBtn.addEventListener('click', function () {
  modalOverlay.classList.add('open');
  document.getElementById('eventName').focus();
});

function closeModal() {
  modalOverlay.classList.remove('open');
}

closeModalBtn.addEventListener('click', closeModal);
cancelModalBtn.addEventListener('click', closeModal);


// ===== CLOSE ON OUTSIDE CLICK =====
modalOverlay.addEventListener('click', function (e) {
  if (e.target === modalOverlay) {
    closeModal();
  }
});


// ===== CLOSE ON ESC =====
document.addEventListener('keydown', function (e) {
  if (e.key === 'Escape' && modalOverlay.classList.contains('open')) {
    closeModal();
  }
});


// ===== BASIC VALIDATION =====
const form = document.getElementById('eventForm');

form.addEventListener('submit', function (e) {

  const name = document.getElementById('eventName').value.trim();
  const address = document.getElementById('eventAddress').value.trim();
  const city = document.getElementById('eventCity').value.trim();
  const pincode = document.getElementById('eventPincode').value.trim();

  if (!name) {
    e.preventDefault();
    const input = document.getElementById('eventName');
    input.focus();
    input.style.borderColor = '#e63946';
    return;
  }

  if (!address) {
    e.preventDefault();
    const input = document.getElementById('eventAddress');
    input.focus();
    input.style.borderColor = '#e63946';
    return;
  }

  if (!city) {
    e.preventDefault();
    const input = document.getElementById('eventCity');
    input.focus();
    input.style.borderColor = '#e63946';
    return;
  }

  if (!pincode || pincode.length !== 6) {
    e.preventDefault();
    const input = document.getElementById('eventPincode');
    input.focus();
    input.style.borderColor = '#e63946';
    return;
  }

});


// ===== RESET BORDER ON INPUT =====
document.getElementById('eventName').addEventListener('input', function () {
  this.style.borderColor = '';
});

document.getElementById('eventAddress').addEventListener('input', function () {
  this.style.borderColor = '';
});

document.getElementById('eventCity').addEventListener('input', function () {
  this.style.borderColor = '';
});

document.getElementById('eventPincode').addEventListener('input', function () {
  this.style.borderColor = '';
});


// ===== TOAST =====
function showToast(message) {

  const toast = document.getElementById('toast');

  toast.textContent = message;
  toast.classList.add('show');

  setTimeout(function () {
    toast.classList.remove('show');
  }, 3000);
}

const matchList = document.getElementById('matchList');

function loadMatches() {
    fetch('/new_common/getData')
        .then(res => res.json())
        .then(users => {
            // Clear current list before adding new ones
            matchList.innerHTML = '';

            if (users.length === 0) {
                matchList.innerHTML = '<div class="empty-state">No matches found yet.</div>';
                return;
            }

            users.forEach(u => {
                // Formatting data from DB
                const interests = u.sports ? u.sports.split(",") : [];
                const randomKm = Math.floor(Math.random() * 15) + 1;
                const randomImg = "https://i.pravatar.cc/64?img=" + Math.floor(Math.random() * 70);

                // Create the element using your CSS classes
                const card = document.createElement('div');
                card.className = 'user-card';

                card.innerHTML = `
                    <img src="${randomImg}" class="user-avatar" alt="${u.name}">
                    <div class="user-info">
                        <div class="user-header">
                            <span class="user-name">${u.name}</span>
                            <span class="tag">${u.level}</span>
                        </div>
                        <div class="user-location">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                            ${u.city} • ${randomKm} km away
                        </div>
                        <div class="user-interests">
                            ${interests.map(sport => `<span class="interest-tag">${sport.trim()}</span>`).join('')}
                        </div>
                        <p class="user-bio">${u.description || 'No description provided.'}</p>
                    </div>
                    <div class="user-actions">
                        <button class="btn-connect" onclick="connectUser('${u.name}', '${u.adminId}')">Connect</button>

                        <a href="/new_common/DiscussionServlet?event_id=${u.eventId}">
                                    <button class="btn-message">Message</button>
                        </a>
                    </div>
                `;
                matchList.appendChild(card);
            });
        })
        .catch(err => console.error("Error loading matches:", err));
}

// Initial load
loadMatches();

// AUTO-UPDATE: Refresh the list every 30 seconds to show changes in DB
setInterval(loadMatches, 30000);


function openDiscussion(eventId) {
     window.location.href = "ChatRoom.html?event_id=" + eventId;
}

function connectUser(eventName, adminId) {
    // 1. Grab the button that was clicked to give visual feedback
    const btn = event.target;
    const originalText = btn.innerText;

    btn.disabled = true;
    btn.innerText = "Connecting...";

    // 2. Prepare the data
    // We don't need to send the 'senderId' because the Servlet
    // will get that from the Session for better security.
    const params = new URLSearchParams();
    params.append('targetAdminId', adminId);
    params.append('eventName', eventName);

    // 3. Fetch call to your Servlet
    fetch('/new_common/ConnectServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(res => res.text())
    .then(status => {
        if (status.trim() === "success") {
            btn.innerText = "Requested";
            btn.style.backgroundColor = "#2e9e6b"; // Success Green
        } else {
            alert("Failed to send request. Please try again.");
            btn.disabled = false;
            btn.innerText = originalText;
        }
    })
    .catch(err => {
        console.error("Connect error:", err);
        btn.disabled = false;
        btn.innerText = originalText;
    });
}
