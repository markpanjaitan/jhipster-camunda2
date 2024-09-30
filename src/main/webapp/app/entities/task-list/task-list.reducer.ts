import axios from 'axios';
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { TaskListDto } from 'app/shared/model/task-list-dto.model';

const apiUrl = 'api/task-lists';

// Define the type for query params
interface IQueryParams {
  sort: string;
}

// Async action to fetch task lists
export const getEntities = createAsyncThunk<TaskListDto[], IQueryParams>(
  'taskList/fetch_entity_list',
  async ({ sort }) => {
    const requestUrl = `${apiUrl}?${sort ? `sort=${sort}&` : ''}cacheBuster=${new Date().getTime()}`;
    const response = await axios.get<TaskListDto[]>(requestUrl);
    return response.data; // Return the data directly
  },
  { serializeError: serializeAxiosError },
);

// New async action to fetch form data
export const fetchFormData = createAsyncThunk<string, { formId: string; processDefinitionKey: string; version: number }>(
  'taskList/fetch_form_data',
  async ({ formId, processDefinitionKey, version }) => {
    const requestUrl = `/api/task-lists/form/${formId}?processDefinitionKey=${processDefinitionKey}&version=${version}`;
    const response = await axios.get<string>(requestUrl);
    return response.data; // Assuming this is the HTML or JSON string response
  },
  { serializeError: serializeAxiosError },
);

// New async action to assign a task
export const assignTask = createAsyncThunk<string, { taskId: string }>(
  'taskList/assign_task',
  async ({ taskId }) => {
    const requestUrl = `/api/task-lists/tasks/${taskId}/assign`;
    const response = await axios.patch<string>(requestUrl);
    return response.data; // Assuming this is the response message from the server
  },
  { serializeError: serializeAxiosError },
);

// New async action to complete a task
export const completeTask = createAsyncThunk<string, { taskId: string }>(
  'taskList/assign_task',
  async ({ taskId }) => {
    const requestUrl = `/api/task-lists/tasks/${taskId}/complete`;
    const response = await axios.patch<string>(requestUrl);
    return response.data; // Assuming this is the response message from the server
  },
  { serializeError: serializeAxiosError },
);

// Slice definition
const taskListSlice = createSlice({
  name: 'taskList',
  initialState: {
    loading: false,
    errorMessage: null,
    entities: [] as TaskListDto[],
  },
  reducers: {},
  extraReducers(builder) {
    builder
      .addCase(getEntities.pending, state => {
        state.loading = true;
        state.errorMessage = null;
      })
      .addCase(getEntities.fulfilled, (state, action) => {
        state.loading = false;
        state.entities = action.payload; // Directly set the payload
      })
      .addCase(getEntities.rejected, (state, action) => {
        state.loading = false;
        state.errorMessage = action.error.message;
      });
  },
});

// Export the reducer
export default taskListSlice.reducer;
