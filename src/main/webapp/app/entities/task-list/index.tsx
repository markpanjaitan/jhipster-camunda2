import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import TaskList from './task-list';
import TaskListForm from 'app/entities/task-list/task-list-form';

const TaskListRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TaskList />} />
    <Route path="form/:formId/:processDefinitionKey/:version" element={<TaskListForm />} />
    {/* Uncomment and add other routes as necessary */}
    {/* <Route path="new" element={<TaskListUpdate />} /> */}
    {/* <Route path=":id"> */}
    {/*   <Route index element={<TaskListDetail />} /> */}
    {/*   <Route path="edit" element={<TaskListUpdate />} /> */}
    {/*   <Route path="delete" element={<TaskListDeleteDialog />} /> */}
    {/* </Route> */}
  </ErrorBoundaryRoutes>
);

export default TaskListRoutes;
