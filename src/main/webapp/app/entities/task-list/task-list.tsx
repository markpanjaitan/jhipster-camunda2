import React, { useEffect, useState } from 'react';
import { Button, Table } from 'reactstrap';
import { Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { assignTask, fetchFormData, getEntities } from './task-list.reducer';
import { useNavigate } from 'react-router-dom';
import { jwtDecode, JwtPayload } from 'jwt-decode';

// Custom JWT Payload interface
interface CustomJwtPayload extends JwtPayload {
  id: string; // Adjust type if necessary
}

export const TaskList = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate(); // Initialize useNavigate
  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(window.location, 'id'), window.location.search));
  const taskListDtos = useAppSelector(state => state.taskList.entities);
  const loading = useAppSelector(state => state.taskList.loading);

  const getAllEntities = () => {
    dispatch(getEntities({ sort: `${sortState.sort},${sortState.order}` }));
  };

  useEffect(() => {
    getAllEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleClick = (event, formId, processDefinitionKey, version, taskId) => {
    handleAssignToMe(event, formId, processDefinitionKey, version, taskId);
  };

  const handleAssignToMe = async (event, formId, processDefinitionKey, version, taskId) => {
    event.preventDefault();

    try {
      const assignResult = await dispatch(assignTask({ taskId }));

      if (assignTask.fulfilled.match(assignResult)) {
        console.log('Task assigned successfully:', assignResult.payload);

        const resultAction = await dispatch(fetchFormData({ formId, processDefinitionKey, version }));

        if (fetchFormData.fulfilled.match(resultAction)) {
          const schema = resultAction.payload;
          navigate(`/task-list/form/${formId}/${processDefinitionKey}/${version}`, { state: { schema } });
        } else {
          throw new Error(resultAction.error.message);
        }
      } else {
        throw new Error(assignResult.error.message);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const getSortIconByFieldName = fieldName => {
    return sortState.sort === fieldName ? (sortState.order === ASC ? faSortUp : faSortDown) : faSort;
  };

  return (
    <div>
      <h2 id="task-list-heading" data-cy="TaskListHeading">
        <Translate contentKey="jhipsterApp.taskList.home.title">Task Lists</Translate>
      </h2>
      <div className="table-responsive">
        {taskListDtos && taskListDtos.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="jhipsterApp.taskList.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('taskDefinitionId')}>
                  <Translate contentKey="jhipsterApp.taskList.taskDefinitionId">Task Definition Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('taskDefinitionId')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="jhipsterApp.taskList.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('processName')}>
                  <Translate contentKey="jhipsterApp.taskList.processName">Process Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('processName')} />
                </th>
                <th className="hand" onClick={sort('taskState')}>
                  <Translate contentKey="jhipsterApp.taskList.taskState">Task State</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('taskState')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {taskListDtos.map((taskListDto, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>{taskListDto.id}</td>
                  <td>{taskListDto.taskDefinitionId}</td>
                  <td>{taskListDto.name}</td>
                  <td>{taskListDto.processName}</td>
                  <td>{taskListDto.taskState}</td>
                  <td>
                    <Button
                      color="success"
                      onClick={event =>
                        handleClick(event, taskListDto.formId, taskListDto.processDefinitionKey, taskListDto.formVersion, taskListDto.id)
                      }
                    >
                      Assign to Me
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="jhipsterApp.taskList.home.notFound">No Task Lists found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default TaskList;
