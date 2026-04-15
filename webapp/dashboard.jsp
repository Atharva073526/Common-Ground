<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    int userId = Integer.parseInt(String.valueOf(session.getAttribute("userId")));
    String userName  = (String) session.getAttribute("userFname");
    String userCity  = (String) session.getAttribute("userCity");
    String userLevel = (String) session.getAttribute("userLevel");
    if (userName  == null) userName  = "User";
    if (userCity  == null) userCity  = "";
    if (userLevel == null) userLevel = "Beginner";
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Common Ground – Events</title>
  <link rel="stylesheet" href="styles.css" />
</head>
<body>

  <nav class="navbar">
    <div class="nav-left">
      <span class="logo-icon">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="3"/>
          <path d="M12 1v4M12 19v4M4.22 4.22l2.83 2.83M16.95 16.95l2.83 2.83M1 12h4M19 12h4M4.22 19.78l2.83-2.83M16.95 7.05l2.83-2.83"/>
        </svg>
      </span>
      <span class="logo-text">Common Ground</span>
    </div>

    <div class="nav-links">
      <a href="/new_common/map.jsp" class="nav-item">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polygon points="3 11 22 2 13 21 11 13 3 11"/>
        </svg>
        <span>Map View</span>
      </a>

      <a href="/new_common/dashboard.jsp" class="nav-item">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="4" width="18" height="18" rx="2"/>
          <line x1="16" y1="2" x2="16" y2="6"/>
          <line x1="8" y1="2" x2="8" y2="6"/>
          <line x1="3" y1="10" x2="21" y2="10"/>
        </svg>
        <span>Events</span>
      </a>

      <a href="/new_common/messages" class="nav-item">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
        </svg>
        <span>Messages</span>
      </a>
    </div>
  </nav>

    <div class="nav-right">
      <div class="notif-wrap" id="notifBell" title="Notifications">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
        <span class="notif-badge" id="notifBadge">1</span>
      </div>
      <img src="https://i.pravatar.cc/36?img=8" alt="<%= userName %>" class="avatar" />
      <div class="profile-info">
        <span class="profile-name"><%= userName %></span>
        <span class="profile-location"><%= userCity %></span>
      </div>
      <button class="logout-btn" title="Logout" onclick="window.location.href='logout'">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
      </button>
    </div>
  </nav>

  <!-- MAIN CONTENT -->
  <main class="main">

    <!-- HERO BANNER -->
    <section class="hero">
      <div class="hero-text">
        <h1>Welcome back, <%= userName %>! 👋</h1>
        <p>You have 1 potential matches nearby</p>
      </div>
      <button class="btn-add-event" id="openModal">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Add Event
      </button>
    </section>

    <!-- STATS CARDS -->
    <section class="stats">
      <div class="stat-card">
        <div class="stat-icon icon-purple">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#7c5cbf" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        </div>
        <div>
          <div class="stat-value">1</div>
          <div class="stat-label">Nearby Matches</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon icon-green">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#2e9e6b" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        </div>
        <div>
          <div class="stat-value" id="upcomingCount">3</div>
          <div class="stat-label">Upcoming Games</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon icon-orange">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#d97706" stroke-width="2"><circle cx="12" cy="8" r="6"/><path d="M15.477 12.89L17 22l-5-3-5 3 1.523-9.11"/></svg>
        </div>
        <div>
          <div class="stat-value"><%= userLevel %></div>
          <div class="stat-label">Your Level</div>
        </div>
      </div>
    </section>

    <!-- RECOMMENDED MATCHES -->
    <section class="matches-section">
      <div class="matches-header">
        <div>
          <h2 class="section-title">Recommended Matches</h2>
          <p class="section-sub">Athletes near you with similar interests and skill level</p>
        </div>
        <button class="btn-filter-toggle" id="filterToggle">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="4" y1="6" x2="20" y2="6"/><line x1="8" y1="12" x2="16" y2="12"/><line x1="11" y1="18" x2="13" y2="18"/></svg>
          Filters
          <span class="filter-count" id="filterCount" style="display:none">0</span>
        </button>
      </div>

      <!-- FILTER PANEL -->
      <div class="filter-panel" id="filterPanel">
        <div class="filter-group">
          <label class="filter-label">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13S3 17 3 10a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
            Location (distance)
          </label>
          <div class="filter-chips">
            <button class="chip active" data-filter="location" data-value="all">All</button>
            <button class="chip" data-filter="location" data-value="5">Within 5 km</button>
            <button class="chip" data-filter="location" data-value="10">Within 10 km</button>
            <button class="chip" data-filter="location" data-value="20">Within 20 km</button>
          </div>
        </div>
        <div class="filter-divider"></div>
        <div class="filter-group">
          <label class="filter-label">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="6"/><path d="M15.477 12.89L17 22l-5-3-5 3 1.523-9.11"/></svg>
            Skill Level
          </label>
          <div class="filter-chips">
            <button class="chip active" data-filter="level" data-value="all">All</button>
            <button class="chip" data-filter="level" data-value="beginner">Beginner</button>
            <button class="chip" data-filter="level" data-value="intermediate">Intermediate</button>
            <button class="chip" data-filter="level" data-value="advanced">Advanced</button>
          </div>
        </div>
        <button class="btn-clear-filters" id="clearFilters">Clear all filters</button>
      </div>

      <!-- MATCH CARDS -->
      <div id="matchList">

      </div>

      <!-- Empty state -->
      <div class="empty-state" id="emptyState" style="display:none;">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#bbb" stroke-width="1.5"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <p>No matches found for the selected filters.</p>
        <button onclick="document.getElementById('clearFilters').click()">Clear filters</button>
      </div>

    </section>

  </main>

  <!-- ADD EVENT MODAL -->
  <div class="modal-overlay" id="modalOverlay">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="modalTitle">
      <div class="modal-header">
        <h3 id="modalTitle">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          Add New Event
        </h3>
        <button class="modal-close" id="closeModal" aria-label="Close">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>
      <form id="eventForm" method="post" action="addEvent">
      <div class="modal-body">
        <div class="form-group">
          <label for="eventName">Event Name</label>
          <input type="text" id="eventName" name="eventName" placeholder="e.g. Weekend Cricket Match" />
        </div>
        <div class="form-row">
          <div class="form-group">
            <label for="eventDate">Date</label>
            <input type="date" id="eventDate" name="eventDate" />
          </div>
          <div class="form-group">
            <label for="eventTime">Time</label>
            <input type="time" id="eventTime" name="eventTime" />
          </div>
        </div>
        <div class="form-group">
          <label for="eventAddress">Address</label>
          <input type="text" id="eventAddress" name="eventAddress" placeholder="e.g. Gokuldham Society, MG Road" />
        </div>
        <div class="form-group">
          <label for="eventCity">City</label>
          <input type="text" id="eventCity" name="eventCity" placeholder="e.g. Mumbai" />
        </div>
        <div class="form-group">
          <label for="eventPincode">Pincode</label>
          <input type="text" id="eventPincode" name="eventPincode" placeholder="e.g. 400011" maxlength="6" />
        </div>
        <div class="form-row">
          <div class="form-group">
            <label for="eventSport">Sport</label>
            <select id="eventSport" name="eventSport">
              <option value="">Select sport</option>
              <option>Cricket</option>
              <option>Football</option>
              <option>Basketball</option>
              <option>Badminton</option>
              <option>Tennis</option>
              <option>Other</option>
            </select>
          </div>
          <div class="form-group">
            <label for="eventLevel">Skill Level</label>
            <select id="eventLevel" name="eventLevel">
              <option value="">Any level</option>
              <option>Beginner</option>
              <option>Intermediate</option>
              <option>Advanced</option>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label for="eventDesc">Description <span class="optional">(optional)</span></label>
          <textarea id="eventDesc" name="eventDesc" rows="3" placeholder="Add details about the event..."></textarea>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn-cancel" id="cancelModal">Cancel</button>
        <button type="submit" class="btn-save" id="saveEvent">Create Event</button>
      </div>
      </form>
    </div>
  </div>

  <!-- SUCCESS TOAST -->
  <div class="toast" id="toast"></div>

  <script src="script.js"></script>
</body>
</html>
