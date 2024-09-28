import React from 'react';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import { Route } from 'react-router-dom';
import TaskList from 'app/entities/task-list';
import CsvFile from 'app/entities/csv-file';
import TaskListForm from 'app/entities/task-list/task-list-form';

/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
        <Route path="task-list/*" element={<TaskList />} />
        <Route path="csv-file/*" element={<CsvFile />} />
        <Route path="task-list/form/*" element={<TaskListForm />} />
      </ErrorBoundaryRoutes>
    </div>
  );
};
