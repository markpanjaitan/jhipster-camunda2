import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './csv-file.reducer';

export const CsvFileDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const csvFileEntity = useAppSelector(state => state.csvFile.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="csvFileDetailsHeading">
          <Translate contentKey="jhipsterApp.csvFile.detail.title">CsvFile</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{csvFileEntity.id}</dd>
          <dt>
            <span id="file">
              <Translate contentKey="jhipsterApp.csvFile.file">File</Translate>
            </span>
          </dt>
          <dd>{csvFileEntity.file}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="jhipsterApp.csvFile.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>
            {csvFileEntity.createdDate ? <TextFormat value={csvFileEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="updatedDate">
              <Translate contentKey="jhipsterApp.csvFile.updatedDate">Updated Date</Translate>
            </span>
          </dt>
          <dd>
            {csvFileEntity.updatedDate ? <TextFormat value={csvFileEntity.updatedDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/csv-file" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/csv-file/${csvFileEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CsvFileDetail;
