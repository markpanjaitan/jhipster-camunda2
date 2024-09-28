import React, { useState } from 'react';
import { Button, Form, FormGroup, Label, Input } from 'reactstrap';
import { useAppDispatch } from 'app/config/store';
import axios from 'axios';

const CsvFileUpload = () => {
  const dispatch = useAppDispatch();
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [bpmnXml, setBpmnXml] = useState('');
  const [dmnXml, setDmnXml] = useState(''); // New state for DMN XML

  const handleFileChange = event => {
    setFile(event.target.files[0]);
    setErrorMessage('');
  };

  const handleUpload = event => {
    event.preventDefault();
    if (!file) {
      setErrorMessage('Please select a CSV file to upload.');
      return;
    }

    setLoading(true);

    const formData = new FormData();
    formData.append('file', file); // Append the file to the form data

    // Upload CSV to convert to BPMN
    axios
      .post('/api/csv-files/csv-upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data', // Set the correct content type
        },
        responseType: 'text', // Expect text response for the BPMN XML
      })
      .then(response => {
        setBpmnXml(response.data); // Set the returned BPMN XML
      })
      .catch(error => {
        console.error('Error uploading file:', error); // Log the error for debugging
        setErrorMessage('Failed to convert CSV to BPMN. Please try again.');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const handleConvertToDmn = event => {
    event.preventDefault();
    if (!file) {
      setErrorMessage('Please select a CSV file to upload.');
      return;
    }

    setLoading(true);

    const formData = new FormData();
    formData.append('file', file); // Append the file to the form data

    // Upload CSV to convert to DMN
    axios
      .post('/api/csv-files/csv-dmn', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        responseType: 'text', // Expect text response for the DMN XML
      })
      .then(response => {
        setDmnXml(response.data); // Set the returned DMN XML
      })
      .catch(error => {
        console.error('Error uploading file for DMN conversion:', error);
        setErrorMessage('Failed to convert CSV to DMN. Please try again.');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <div>
      <h2 id="csv-file-upload-heading">Upload CSV File</h2>
      <Form onSubmit={handleUpload}>
        <FormGroup>
          <Label for="file">CSV File</Label>
          <Input type="file" id="file" accept=".csv" onChange={handleFileChange} required />
        </FormGroup>
        {errorMessage && <div className="alert alert-danger">{errorMessage}</div>}
        <Button color="primary" type="submit" disabled={loading}>
          {loading ? 'Uploading...' : 'Convert to BPMN'}
        </Button>
        <Button
          color="success" // Change the color to green
          onClick={handleConvertToDmn}
          disabled={loading}
          style={{ marginLeft: '10px' }} // Add space between buttons
        >
          {loading ? 'Converting...' : 'Convert to DMN'}
        </Button>
      </Form>
      {bpmnXml && (
        <div>
          <h3>Generated BPMN XML</h3>
          <pre>{bpmnXml}</pre>
        </div>
      )}
      {dmnXml && (
        <div>
          <h3>Generated DMN XML</h3>
          <pre>{dmnXml}</pre>
        </div>
      )}
    </div>
  );
};

export default CsvFileUpload;
