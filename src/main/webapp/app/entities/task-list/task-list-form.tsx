import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { completeTask } from 'app/entities/task-list/task-list.reducer';
import { AppDispatch } from 'app/config/store';
import { useDispatch } from 'react-redux';

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
  const { schema: formSchema, taskId } = location.state || {};
  const [formData, setFormData] = useState<{ [key: string]: any }>({});
  const [formError, setFormError] = useState<string | null>(null);
  const dispatch: AppDispatch = useDispatch();
  const navigate = useNavigate();

  const components = formSchema?.components || [];

  const handleChange = (key: string, value: any) => {
    setFormData(prev => ({
      ...prev,
      [key]: value,
    }));
  };

  const createVariables = (schema: string, selectedValues: { [key: string]: any }) => {
    const parsedSchema = JSON.parse(schema);
    const variables = [];

    parsedSchema.components.forEach(component => {
      if (component.key) {
        const selectedValue = selectedValues[component.key];
        if (selectedValue !== undefined) {
          variables.push({
            name: component.key,
            value: JSON.stringify(selectedValue),
          });
        }
      }
    });

    return {
      variables,
    };
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    submitForm();
  };

  const submitForm = async () => {
    try {
      const selectedValues = { ...formData };
      const variableData = createVariables(JSON.stringify(formSchema), selectedValues);

      console.log('selectedValues =', selectedValues);
      console.log('variableData =', variableData);

      const resultAction = await dispatch(completeTask({ taskId, strVariables: JSON.stringify(variableData) })); // Ensure to stringify

      if (completeTask.fulfilled.match(resultAction)) {
        alert('Form submitted successfully!');
        navigate('/task-list');
      } else {
        throw new Error(resultAction.error.message);
      }
    } catch (error) {
      setFormError('Failed to submit form: ' + error.message);
    }
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
      case 'select': // Changed 'dropdown' to 'select' to match schema
        return (
          <div key={component.id}>
            <label>{component.label}</label>
            <select
              value={formData[component.key] || ''} // Set the selected value
              onChange={e => handleChange(component.key || '', e.target.value)}
            >
              <option value="" disabled>
                Select an option
              </option>
              {component.values?.map(option => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
            {component.description && <small>{component.description}</small>}
          </div>
        );
      default:
        return null;
    }
  };

  if (formError) return <p>Error: {formError}</p>;

  return (
    <form onSubmit={handleSubmit}>
      <div style={{ marginBottom: '20px' }}>{components.map(renderComponent)}</div>
      <button type="submit">Submit</button>
    </form>
  );
};

export default TaskListForm;
