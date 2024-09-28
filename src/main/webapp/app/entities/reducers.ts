/* jhipster-needle-add-reducer-import - JHipster will add reducer here */
import taskList from 'app/entities/task-list/task-list.reducer';
import csvFile from 'app/entities/csv-file/csv-file.reducer';

const entitiesReducers = {
  taskList,
  csvFile,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
