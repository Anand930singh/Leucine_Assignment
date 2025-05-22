import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createAuthAxios, getUserData, logout } from '../utils/auth';
import '../style/home.css';
import ReactMarkdown from 'react-markdown';

function Home() {
  const navigate = useNavigate();
  const [todos, setTodos] = useState([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [showEditForm, setShowEditForm] = useState(false);
  const [newTodo, setNewTodo] = useState('');
  const [editingTodo, setEditingTodo] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [filter, setFilter] = useState({
    status: null,
    search: '',
    sortBy: 'createdAt',
    sortOrder: 'DESC'
  });
  const [summary, setSummary] = useState('');
  const [showSummary, setShowSummary] = useState(false);
  const [userData, setUserData] = useState(null);

  const itemsPerPage = 6;
  const authAxios = createAuthAxios();

  useEffect(() => {
    console.log('Home component mounted');
    try {
      const user = getUserData();
      console.log('User data:', user);
      if (!user) {
        console.log('No user data found, redirecting to home');
        navigate('/');
        return;
      }
      setUserData(user);
      fetchTodos();
    } catch (err) {
      console.error('Error in useEffect:', err);
      setError('Error loading user data');
    }
  }, [currentPage, filter]);

  const fetchTodos = async () => {
    try {
      console.log('Fetching todos...');
      setLoading(true);
      const response = await authAxios.post('/todos/filter', {
        ...filter,
        page: currentPage - 1,
        size: itemsPerPage
      });
      console.log('Todos response:', response.data);

      if (response.data.status === 200) {
        setTodos(response.data.data.content);
        setTotalPages(Math.ceil(response.data.data.totalElements / itemsPerPage));
      }
    } catch (err) {
      console.error('Error fetching todos:', err);
      setError(err.response?.data?.message || 'Error fetching todos');
      if (err.response?.status === 401) {
        navigate('/');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const handleAddTodo = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      const response = await authAxios.post('/todos', { task: newTodo, status: "PENDING" });

      if (response.data.status === 201) {
        setNewTodo('');
        setShowAddForm(false);
        fetchTodos();
      }
    } catch (err) {
      console.error('Error adding todo:', err);
      setError(err.response?.data?.message || 'Error adding todo');
    } finally {
      setLoading(false);
    }
  };

  const handleEditTodo = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      const response = await authAxios.put(`/todos/${editingTodo.id}`, {
        task: newTodo,
        status: editingTodo.status
      });

      if (response.data.status === 200) {
        setNewTodo('');
        setEditingTodo(null);
        setShowEditForm(false);
        fetchTodos();
      }
    } catch (err) {
      console.error('Error updating todo:', err);
      setError(err.response?.data?.message || 'Error updating todo');
    } finally {
      setLoading(false);
    }
  };

  const startEditing = (todo) => {
    setEditingTodo(todo);
    setNewTodo(todo.task);
    setShowEditForm(true);
  };

  const toggleTodo = async (id) => {
    try {
      const todo = todos.find(t => t.id === id);
      const response = await authAxios.put(`/todos/${id}`, {
        task: todo.task,
        status: todo.status === "PENDING" ? "COMPLETED" : "PENDING"
      });

      if (response.data.status === 200) {
        fetchTodos();
      }
    } catch (err) {
      console.error('Error updating todo:', err);
      setError(err.response?.data?.message || 'Error updating todo');
    }
  };

  const deleteTodo = async (id) => {
    try {
      const response = await authAxios.delete(`/todos/${id}`);

      if (response.data.status === 200) {
        fetchTodos();
      }
    } catch (err) {
      console.error('Error deleting todo:', err);
      setError(err.response?.data?.message || 'Error deleting todo');
    }
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilter(prev => ({
      ...prev,
      [name]: value
    }));
    setCurrentPage(1);
  };

  const handleSummarize = async () => {
    try {
      setLoading(true);
      const response = await authAxios.get('/llm/summarize-tasks');

      if (response.data.status === 200) {
        setSummary(response.data.data);
        setShowSummary(true);
      }
    } catch (err) {
      console.error('Error generating summary:', err);
      setError(err.response?.data?.message || 'Error generating summary');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="home-page">
        <div className="home-container">
          <div className="loading-message">Loading...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="home-page">
        <div className="home-container">
          <div className="error-message">{error}</div>
          <button onClick={() => navigate('/')} className="back-button">
            Back to Login
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="home-page">
      <div className="home-container">
        <div className="home-header">
          <h1 className="home-title">My Todo List</h1>
          <div className="user-info">
            <span className="user-name">Welcome, {userData?.username || 'User'}</span>
            <button className="logout-button" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="todo-container">
          <div className="todo-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.75rem', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '1.25rem' }}>
            <button 
              className="summary-button"
              onClick={handleSummarize}
              disabled={loading}
              style={{ minWidth: 150 }}
            >
              Summarize Tasks
            </button>
            <button 
              className="add-todo-button"
              onClick={() => setShowAddForm(true)}
              disabled={loading}
              style={{ minWidth: 150 }}
            >
              Add New Task
            </button>
          </div>

          {/* Summary Centered Modal */}
          {showSummary && (
            <>
              <div className="modal-overlay" onClick={() => setShowSummary(false)}></div>
              <div className="summary-modal">
                <button className="close-summary-button modal-close" onClick={() => setShowSummary(false)} title="Close">
                  <i className="fas fa-times"></i>
                </button>
                <h3>Task Summary</h3>
                <div className="summary-modal-content">
                  <ReactMarkdown>{summary}</ReactMarkdown>
                </div>
              </div>
            </>
          )}

          <div className="todo-table">
            <table>
              <thead>
                <tr className="filter-row">
                  <th>
                    <select
                      name="status"
                      value={filter.status || ''}
                      onChange={handleFilterChange}
                      className="column-filter-select"
                    >
                      <option value="">All</option>
                      <option value="PENDING">Pending</option>
                      <option value="COMPLETED">Completed</option>
                    </select>
                  </th>
                  <th>
                    <input
                      type="text"
                      name="task"
                      value={filter.task || ''}
                      onChange={handleFilterChange}
                      className="column-filter-input"
                      placeholder="Search task..."
                    />
                  </th>
                  <th>
                    <input
                      type="date"
                      name="createdAt"
                      value={filter.createdAt || ''}
                      onChange={handleFilterChange}
                      className="column-filter-input"
                    />
                  </th>
                  <th>
                    <input
                      type="date"
                      name="updatedAt"
                      value={filter.updatedAt || ''}
                      onChange={handleFilterChange}
                      className="column-filter-input"
                    />
                  </th>
                  <th style={{textAlign: 'center'}}>
                    <button
                      className="clear-filter-btn"
                      onClick={() => {
                        setFilter({ status: '', task: '', createdAt: '', updatedAt: '' });
                        setCurrentPage(1);
                      }}
                      title="Clear all filters"
                      type="button"
                    >
                      <i className="fas fa-times"></i> Clear
                    </button>
                  </th>
                </tr>
                <tr>
                  <th>Status</th>
                  <th>Task</th>
                  <th>Created</th>
                  <th>Updated</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {todos.map(todo => (
                  <tr key={todo.id}>
                    <td>
                      <input
                        type="checkbox"
                        className="todo-checkbox"
                        checked={todo.status === "COMPLETED"}
                        onChange={() => toggleTodo(todo.id)}
                      />
                    </td>
                    <td>
                      <span className="todo-text" style={{
                        textDecoration: todo.status === "COMPLETED" ? 'line-through' : 'none',
                        color: todo.status === "COMPLETED" ? '#94a3b8' : '#e2e8f0'
                      }}>
                        {todo.task}
                      </span>
                    </td>
                    <td>{new Date(todo.createdAt).toLocaleDateString()}</td>
                    <td>{new Date(todo.updatedAt).toLocaleDateString()}</td>
                    <td className="action-buttons">
                      <button 
                        className="todo-action-button edit"
                        onClick={() => startEditing(todo)}
                        title="Edit Task"
                      >
                        <i className="fas fa-edit"></i>
                      </button>
                      <button 
                        className="todo-action-button delete"
                        onClick={() => deleteTodo(todo.id)}
                        title="Delete Task"
                      >
                        <i className="fas fa-trash"></i>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="pagination">
            <button
              onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
              disabled={currentPage === 1 || loading}
              className="pagination-button"
            >
              Previous
            </button>
            <span className="page-info">
              Page {currentPage} of {totalPages}
            </span>
            <button
              onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
              disabled={currentPage === totalPages || loading}
              className="pagination-button"
            >
              Next
            </button>
          </div>
        </div>

        {showAddForm && (
          <div className="modal-overlay">
            <div className="modal-content">
              <form className="todo-form" onSubmit={handleAddTodo}>
                <div className="form-group">
                  <label htmlFor="newTodo">New Task</label>
                  <input
                    type="text"
                    id="newTodo"
                    value={newTodo}
                    onChange={(e) => setNewTodo(e.target.value)}
                    placeholder="Enter your task"
                    required
                  />
                </div>
                <div className="form-actions">
                  <button 
                    type="button" 
                    className="cancel-button"
                    onClick={() => setShowAddForm(false)}
                  >
                    Cancel
                  </button>
                  <button 
                    type="submit" 
                    className="submit-button"
                    disabled={loading}
                  >
                    {loading ? 'Adding...' : 'Add Task'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {showEditForm && (
          <div className="modal-overlay">
            <div className="modal-content">
              <form className="todo-form" onSubmit={handleEditTodo}>
                <div className="form-group">
                  <label htmlFor="editTodo">Edit Task</label>
                  <input
                    type="text"
                    id="editTodo"
                    value={newTodo}
                    onChange={(e) => setNewTodo(e.target.value)}
                    placeholder="Edit your task"
                    required
                  />
                </div>
                <div className="form-actions">
                  <button 
                    type="button" 
                    className="cancel-button"
                    onClick={() => {
                      setShowEditForm(false);
                      setEditingTodo(null);
                      setNewTodo('');
                    }}
                  >
                    Cancel
                  </button>
                  <button 
                    type="submit" 
                    className="submit-button"
                    disabled={loading}
                  >
                    {loading ? 'Saving...' : 'Save Changes'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Home;