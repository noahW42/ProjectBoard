import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function NavBar() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetch("/api/me", { credentials: "include" })
      .then(res => {
        if (!res.ok) throw new Error();
        return res.json();
      })
      .then(data => setUser(data))
      .catch(() => {
        setUser(null);
      });
  }, []);

  const handleLogout = () => {
    fetch("/api/logout", {
      method: "POST",
      credentials: "include",
    }).finally(() => {
      navigate("/");
    });
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <span
          className="navbar-brand"
          onClick={() => navigate("/home")}
          style={{ cursor: "pointer" }}
        >
          ProjectBoard
        </span>

        <div className="navbar-links">
          <span className="nav-link" onClick={() => navigate("/home")}>
            Projects
          </span>
        </div>
      </div>

      <div className="navbar-right">
        <span className="navbar-user">
          {user ? ` ${user.username}` : ""}
        </span>
        <button className="logout-btn" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </nav>
  );
}

export default NavBar;