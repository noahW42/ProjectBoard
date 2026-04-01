import { useEffect, useState } from "react";
import navBar from "../components/Navbar";  
import NavBar from "../components/Navbar";
function Home() {
  const [user, setUser] = useState(null);
  const [projects, setProjects] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [newTitle, setNewTitle] = useState("");
  const [newDescription, setNewDescription] = useState("");
  const [error, setError] = useState("");
  const [inviteProjectId, setInviteProjectId] = useState(null);
  const [inviteUsername, setInviteUsername] = useState("");
  const [inviteError, setInviteError] = useState("");
  const [inviteSuccess, setInviteSuccess] = useState("");

  useEffect(() => {
    fetch("/api/me", { credentials: "include" })
      .then(res => {
        if (!res.ok) throw new Error("Not logged in");
        return res.json();
      })
      .then(data => {
        setUser(data);
        return fetch("/api/projects", { credentials: "include" });
      })
      .then(res => res.json())
      .then(data => setProjects(data))
      .catch(() => { window.location.href = "/"; });
  }, []);

  const handleCreateProject = () => {
    if (!newTitle.trim()) {
      setError("Title is required");
      return;
    }
    fetch("/api/projects", {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title: newTitle, description: newDescription })
    })
      .then(res => res.json())
      .then(newProject => {
        setProjects(prev => [...prev, newProject]);
        setShowModal(false);
        setNewTitle("");
        setNewDescription("");
        setError("");
      })
      .catch(() => setError("Failed to create project"));
  };

  const handleInvite = () => {
    if (!inviteUsername.trim()) return;
    setInviteError("");
    setInviteSuccess("");

    fetch(`/api/projects/${inviteProjectId}/invite`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: inviteUsername })
    })
      .then(res => {
        if (!res.ok) return res.text().then(t => { throw new Error(t); });
        return res.text();
      })
      .then(() => {
        setInviteSuccess("User invited!");
        setInviteUsername("");
      })
      .catch(err => setInviteError(err.message || "Failed to invite user"));
  };

  const closeModal = () => {
    setShowModal(false);
    setNewTitle("");
    setNewDescription("");
    setError("");
  };

  const closeInviteModal = () => {
    setInviteProjectId(null);
    setInviteUsername("");
    setInviteError("");
    setInviteSuccess("");
  };

  return (
    <>
      <NavBar />
    

      <div className="home-wrapper">
        <div className="home-top">
          <div>
            <h1>Dashboard</h1>
            {user && <p className="home-welcome">Welcome back, {user.username}!</p>}
          </div>
          <button className="create-project-btn" onClick={() => setShowModal(true)}>
            + New Project
          </button>
        </div>

        <h2>Your Projects</h2>
        <div className="project-grid">
          {projects.map(project => (
            <div key={project.id} className="project-card">
              <span className="project-card-title">{project.title}</span>
              {project.description && (
                <span className="project-card-desc">{project.description}</span>
              )}
              <div style={{ display: "flex", gap: "8px" }}>
                <button onClick={() => window.location.href = `/project/${project.id}`}>
                  View Board
                </button>
                {project.user?.id === user?.id && (
                  <button
                    className="invite-btn"
                    onClick={() => setInviteProjectId(project.id)}
                  >
                    Invite
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Create Project Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal-card" onClick={e => e.stopPropagation()}>
            <h3>New Project</h3>
            <input
              type="text"
              placeholder="Project title"
              value={newTitle}
              onChange={e => setNewTitle(e.target.value)}
              className="modal-input"
            />
            <textarea
              placeholder="Description (optional)"
              value={newDescription}
              onChange={e => setNewDescription(e.target.value)}
              className="modal-textarea"
              rows={3}
            />
            {error && <p className="login-error">{error}</p>}
            <div className="modal-actions">
              <button className="modal-confirm-btn" onClick={handleCreateProject}>Create</button>
              <button className="modal-cancel-btn" onClick={closeModal}>Cancel</button>
            </div>
          </div>
        </div>
      )}

      {/* Invite Modal */}
      {inviteProjectId && (
        <div className="modal-overlay" onClick={closeInviteModal}>
          <div className="modal-card" onClick={e => e.stopPropagation()}>
            <h3>Invite User</h3>
            <input
              type="text"
              placeholder="Enter username"
              value={inviteUsername}
              onChange={e => setInviteUsername(e.target.value)}
              className="modal-input"
              onKeyDown={e => e.key === "Enter" && handleInvite()}
            />
            {inviteError && <p className="login-error">{inviteError}</p>}
            {inviteSuccess && <p className="login-success">{inviteSuccess}</p>}
            <div className="modal-actions">
              <button className="modal-confirm-btn" onClick={handleInvite}>Invite</button>
              <button className="modal-cancel-btn" onClick={closeInviteModal}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default Home;