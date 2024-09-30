import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

interface Component {
  text?: string;
  label: string;
  type: string;
  id: string;
  key?: string;
  description?: string;
  defaultValue?: any;
  decimalDigits?: number;
  values?: Array<{ label: string; value: string }>;
}

interface Schema {
  components: Component[];
}

const TaskListForm: React.FC = () => {
  const location = useLocation();
  const { schema } = location.state || {}; // Access schema from location state
  const [formData, setFormData] = useState<{ [key: string]: any }>({});
  const [error, setError] = useState<string | null>(null);

  if (!schema) {
    return <p>No schema provided!</p>; // Handle case where schema is not available
  }

  const components = schema.components;

  const handleChange = (key: string, value: any) => {
    setFormData(prev => ({
      ...prev,
      [key]: value,
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    axios
      .post('/api/submit', formData)
      .then(() => {
        alert('Form submitted successfully!');
      })
      .catch(() => {
        setError('Failed to submit form');
      });
  };

  const renderComponent = (component: Component) => {
    switch (component.type) {
      case 'text':
        return (
          <div key={component.id}>
            <label>{component.label}</label>
            <p>{component.text}</p>
          </div>
        );
      case 'radio':
        return (
          <div key={component.id}>
            <label>{component.label}</label>
            {component.values?.map(option => (
              <div key={option.value}>
                <input
                  type="radio"
                  name={component.key}
                  value={option.value}
                  onChange={() => handleChange(component.key || '', option.value)}
                />
                {option.label}
              </div>
            ))}
          </div>
        );
      case 'number':
        return (
          <div key={component.id}>
            <label>{component.label}</label>
            <input
              type="number"
              defaultValue={component.defaultValue || ''}
              onChange={e => handleChange(component.key || '', Number(e.target.value))}
            />
            {component.description && <small>{component.description}</small>}
          </div>
        );
      default:
        return null;
    }
  };

  if (error) return <p>Error: {error}</p>;

  return (
    <form onSubmit={handleSubmit}>
      <div style={{ marginBottom: '20px' }}>{components.map(renderComponent)}</div>
      <button type="submit">Submit</button>
    </form>
  );
};

export default TaskListForm;
