import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CsvFile from './csv-file';
import CsvFileDetail from './csv-file-detail';
import CsvFileUpdate from './csv-file-update';
import CsvFileDeleteDialog from './csv-file-delete-dialog';

const CsvFileRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CsvFile />} />
    <Route path="new" element={<CsvFileUpdate />} />
    <Route path=":id">
      <Route index element={<CsvFileDetail />} />
      <Route path="edit" element={<CsvFileUpdate />} />
      <Route path="delete" element={<CsvFileDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CsvFileRoutes;
