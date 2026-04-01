import { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import Column from "../components/Column";
import { useBoardSocket } from "../hooks/useBoardSocket.jsx";
import NavBar from "../components/Navbar.jsx";

// ─────────────────────────────────────────────
// PROJECT BOARD PAGE
// Main board view: columns, tasks, drag & drop
// ─────────────────────────────────────────────

function ProjectBoardPage() {
  const { id } = useParams();
  const [columns, setColumns] = useState([]);
  const [newColumnName, setNewColumnName] = useState("");
  const [projectTitle, setProjectTitle] = useState("");
  const currentUserIdRef = useRef(null);

  // ─────────────────────────────────────────────
  // INITIAL DATA FETCHING
  // ─────────────────────────────────────────────

  // Get the current logged-in user
  useEffect(() => {
    fetch("/api/me", { credentials: "include" })
      .then(res => res.json())
      .then(data => {
        currentUserIdRef.current = data.id;
        window.currentUserId = data.id;
      });
  }, []);

  // Load the project's columns and title
  useEffect(() => {
    fetch(`/api/projects/${id}`, { credentials: "include" })
      .then(res => res.json())
      .then(data => {
        setProjectTitle(data.title);
        setColumns(data.columns || []);
      });
  }, [id]);

  // ─────────────────────────────────────────────
  // WEBSOCKET EVENT HANDLER
  // Syncs board state with other connected users
  // ─────────────────────────────────────────────

  const handleSocketEvent = (event) => {
    // Ignore events with no data or from the current user
    if (!currentUserIdRef.current || !event.data) return;
    if (event.data.senderId === currentUserIdRef.current) return;

    switch (event.type) {
      case "COLUMNS_UPDATED":
        setColumns(event.data.sort((a, b) => a.position - b.position));
        break;

      case "COLUMN_ADDED":
        setColumns(prev => {
          if (prev.some(c => c.id === event.data.id)) return prev;
          return [...prev, { ...event.data, tasks: [] }];
        });
        break;

      case "COLUMN_DELETED":
        setColumns(prev => prev.filter(c => c.id !== event.data.columnId));
        break;

      case "TASK_ADDED":
        setColumns(prev => prev.map(col => {
          if (col.id !== event.data.columnId) return col;
          if (col.tasks?.some(t => t.id === event.data.task.id)) return col;
          return { ...col, tasks: [...(col.tasks || []), event.data.task] };
        }));
        break;

      case "TASK_MOVED":
        setColumns(prev => {
          const newCols = prev.map(c => ({ ...c, tasks: [...(c.tasks || [])] }));
          const sourceCol = newCols.find(c => c.id === event.data.sourceColumnId);
          const destCol = newCols.find(c => c.id === event.data.destColumnId);
          if (!sourceCol || !destCol) return prev;

          const taskIndex = sourceCol.tasks.findIndex(t => t.id === event.data.task.id);
          if (taskIndex !== -1) sourceCol.tasks.splice(taskIndex, 1);
          destCol.tasks.splice(event.data.destIndex, 0, event.data.task);
          return newCols;
        });
        break;

      case "TASK_UPDATED":
        setColumns(prev =>
          prev.map(col => ({
            ...col,
            tasks: col.tasks?.map(task =>
              task.id === event.data.task.id ? event.data.task : task
            )
          }))
        );
        break;

      default:
        console.warn("Unhandled WebSocket event:", event.type);
    }
  };

  const { sendEvent } = useBoardSocket(id, handleSocketEvent);
  window.sendBoardEvent = sendEvent;

  // ─────────────────────────────────────────────
  // COLUMN ACTIONS
  // ─────────────────────────────────────────────

  const handleAddColumn = () => {
    if (!newColumnName.trim()) return;

    fetch(`/api/projects/${id}/columns`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name: newColumnName })
    })
      .then(res => res.json())
      .then(data => {
        if (!data.id) return;
        setColumns(prev => [...prev, { ...data, tasks: [] }]);
        setNewColumnName("");
        sendEvent({ type: "COLUMN_ADDED", data, senderId: currentUserIdRef.current });
      })
      .catch(console.error);
  };

  const handleDeleteColumn = (columnId) => {
    fetch(`/api/columns/${columnId}`, {
      method: "DELETE",
      credentials: "include"
    }).then(() => {
      setColumns(prev => prev.filter(c => c.id !== columnId));
      sendEvent({ type: "COLUMN_DELETED", data: { columnId }, senderId: currentUserIdRef.current });
    });
  };

  // ─────────────────────────────────────────────
  // TASK ACTIONS
  // ─────────────────────────────────────────────

  const handleAddTask = (columnId, title) => {
    fetch(`/api/columns/${columnId}/tasks`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title })
    })
      .then(res => res.json())
      .then(newTask => {
        setColumns(prev => prev.map(c =>
          c.id === columnId ? { ...c, tasks: [...(c.tasks || []), newTask] } : c
        ));
        sendEvent({ type: "TASK_ADDED", data: { columnId, task: newTask, senderId: currentUserIdRef.current } });
      });
  };

  // ─────────────────────────────────────────────
  // DRAG & DROP HANDLER
  // Handles reordering columns and moving tasks
  // ─────────────────────────────────────────────

  const onDragEnd = (result) => {
    const { source, destination, type } = result;
    if (!destination) return;

    // — Reorder columns —
    if (type === "COLUMN") {
      const reordered = Array.from(columns);
      const [moved] = reordered.splice(source.index, 1);
      reordered.splice(destination.index, 0, moved);
      setColumns(reordered);

      fetch(`/api/projects/${id}/columns/reorder`, {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(reordered.map(c => c.id))
      }).catch(console.error);
      return;
    }

    // — Move task within the same column —
    if (source.droppableId === destination.droppableId) {
      const colIndex = columns.findIndex(c => c.id.toString() === source.droppableId);
      const newTasks = Array.from(columns[colIndex].tasks);
      const [moved] = newTasks.splice(source.index, 1);
      newTasks.splice(destination.index, 0, moved);
      const newColumns = [...columns];
      newColumns[colIndex].tasks = newTasks;
      setColumns(newColumns);
      return;
    }

    // — Move task to a different column —
    const sourceColIndex = columns.findIndex(c => c.id.toString() === source.droppableId);
    const destColIndex = columns.findIndex(c => c.id.toString() === destination.droppableId);
    const sourceTasks = Array.from(columns[sourceColIndex].tasks);
    const destTasks = Array.from(columns[destColIndex].tasks);
    const [movedTask] = sourceTasks.splice(source.index, 1);

    // Optimistically update UI position
    destTasks.splice(destination.index, 0, movedTask);
    const optimisticColumns = [...columns];
    optimisticColumns[sourceColIndex].tasks = sourceTasks;
    optimisticColumns[destColIndex].tasks = destTasks;
    setColumns(optimisticColumns);

    // Persist to backend, then sync full task data (preserves description, etc.)
    fetch(`/api/tasks/${movedTask.id}/move`, {
      method: "PUT",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ columnId: Number(destination.droppableId) })
    })
      .then(res => res.json())
      .then(updatedTask => {
        // Replace optimistic task with full server response
        setColumns(prev => prev.map(col => ({
          ...col,
          tasks: col.tasks?.map(t => t.id === updatedTask.id ? updatedTask : t)
        })));

        // Broadcast the full task (with description) to other users
        sendEvent({
          type: "TASK_MOVED",
          data: {
            task: updatedTask,
            sourceColumnId: columns[sourceColIndex].id,
            destColumnId: columns[destColIndex].id,
            destIndex: destination.index,
            senderId: currentUserIdRef.current
          }
        });
      });
  };

  // ─────────────────────────────────────────────
  // RENDER
  // ─────────────────────────────────────────────

  return (
    <>
      <NavBar />

      <div className="board-wrapper">
        <div className="board-header">
          <h1>{projectTitle || "Project Board"}</h1>
        </div>

        {/* Add Column Form */}
        <div className="add-column-form">
          <input
            type="text"
            placeholder="New column name"
            value={newColumnName}
            onChange={e => setNewColumnName(e.target.value)}
          />
          <button onClick={handleAddColumn}>Add Column</button>
        </div>

        {/* Drag & Drop Board */}
        <DragDropContext onDragEnd={onDragEnd}>
          <Droppable droppableId="all-columns" direction="horizontal" type="COLUMN">
            {(provided) => (
              <div
                ref={provided.innerRef}
                {...provided.droppableProps}
                className="columns-container"
              >
                {columns.map((column, index) => (
                  <Draggable key={column.id} draggableId={`column-${column.id}`} index={index}>
                    {(provided) => (
                      <div
                        ref={provided.innerRef}
                        {...provided.draggableProps}
                        style={provided.draggableProps.style}
                      >
                        <Column
                          column={column}
                          onDeleteColumn={handleDeleteColumn}
                          onAddTask={handleAddTask}
                          dragHandleProps={provided.dragHandleProps}
                        />
                      </div>
                    )}
                  </Draggable>
                ))}
                {provided.placeholder}
              </div>
            )}
          </Droppable>
        </DragDropContext>
      </div>
    </>
  );
}

export default ProjectBoardPage;