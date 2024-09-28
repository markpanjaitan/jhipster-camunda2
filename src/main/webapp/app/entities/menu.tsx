import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/task-list">
        <Translate contentKey="global.menu.entities.taskList" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/csv-file">
        <Translate contentKey="global.menu.entities.csvFile" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
