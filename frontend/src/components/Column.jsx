import { useState } from "react";
import { Droppable, Draggable } from "@hello-pangea/dnd";
import Task from "./Task";

function Column({ column, onDeleteColumn, onAddTask, dragHandleProps }) {
  const [newTaskTitle, setNewTaskTitle] = useState("");
  const [showInput, setShowInput] = useState(false);

  if (!column || !column.id) return null;

  const handleAddTask = () => {
    if (!newTaskTitle.trim()) return;
    onAddTask(column.id, newTaskTitle);
    setNewTaskTitle("");
    setShowInput(false);
  };

  return (
    <div className="column-card">
      <div className="column-header">
        <div className="column-header-left">
          <span className="column-drag-handle" {...dragHandleProps} title="Drag to reorder">⠿</span>
          <h3>{column.name}</h3>
        </div>
        <button className="column-delete-btn" onClick={() => onDeleteColumn(column.id)} title="Delete column">DELETE</button>
      </div>

      <Droppable droppableId={column.id.toString()} type="TASK">
        {(provided) => (
          <div ref={provided.innerRef} {...provided.droppableProps} style={{ minHeight: "200px" }}>
            {column.tasks?.filter(task => task && task.id != null).map((task, index) => (
              <Draggable key={task.id} draggableId={task.id.toString()} index={index}>
                {(provided) => (
                  <div
                    ref={provided.innerRef}
                    {...provided.draggableProps}
                    {...provided.dragHandleProps}
                    style={{ ...provided.draggableProps.style }}
                  >
                    <Task task={task} />
                  </div>
                )}
              </Draggable>
            ))}
            {provided.placeholder}
          </div>
        )}
      </Droppable>

      {showInput ? (
        <div style={{ marginTop: "10px" }}>
          <input
            className="add-task-input"
            type="text"
            placeholder="Task title"
            value={newTaskTitle}
            onChange={e => setNewTaskTitle(e.target.value)}
            onKeyDown={e => e.key === "Enter" && handleAddTask()}
            autoFocus
          />
          <div className="add-task-actions">
            <button onClick={handleAddTask}>Add</button>
            <button onClick={() => { setShowInput(false); setNewTaskTitle(""); }}>Cancel</button>
          </div>
        </div>
      ) : (
        <button className="add-task-btn" onClick={() => setShowInput(true)}>+ Add Task</button>
      )}
    </div>
  );
}

export default Column;