import { useState, useEffect } from "react";  // ← add useEffect

function Task({ task }) {
  const [expanded, setExpanded] = useState(false);
  const [editing, setEditing] = useState(false);
  const [description, setDescription] = useState(task.description || "");
  const [saved, setSaved] = useState(task.description || "");

  // ← Sync local state when task prop changes (e.g. after moving columns)
  useEffect(() => {
    setDescription(task.description || "");
    setSaved(task.description || "");
  }, [task.id, task.description]);

  const handleSave = () => {
    fetch(`/api/tasks/${task.id}`, {
      method: "PUT",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title: task.title, description })
    })
      .then(res => res.json())
      .then(updatedTask => {
        setSaved(updatedTask.description);
        setEditing(false);

      
        if (window.sendBoardEvent) {
          window.sendBoardEvent({
            type: "TASK_UPDATED",
            data: {
              task: updatedTask,
              senderId: window.currentUserId
            }
          });
        }
      });
  };

  const handleCancel = () => {
    setDescription(saved);
    setEditing(false);
  };

  return (
    <div className="task-card">
      <div className="task-header" onClick={() => setExpanded(prev => !prev)}>
        <span className="task-title">{task.title}</span>
        <span className="task-chevron">{expanded ? "▲" : "▼"}</span>
      </div>

      {expanded && (
        <div className="task-body">
          {editing ? (
            <>
              <textarea
                className="task-desc-input"
                value={description}
                onChange={e => setDescription(e.target.value)}
                placeholder="Add a description..."
                rows={3}
                autoFocus
              />
              <div className="task-desc-actions">
                <button className="task-save-btn" onClick={handleSave}>Save</button>
                <button className="task-cancel-btn" onClick={handleCancel}>Cancel</button>
              </div>
            </>
          ) : (
            <div className="task-desc-view" onClick={() => setEditing(true)}>
              {saved
                ? <p className="task-desc-text">{saved}</p>
                : <p className="task-desc-placeholder">Add a description...</p>
              }
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default Task;