import React from 'react';
import { storiesOf } from '@storybook/react';
import { action } from '@storybook/addon-actions';
import { FormPanel } from '@launcher/component';
import { GitInfoLoader } from '../../loaders/git-info-loader';
import { UserRepositoryPicker } from '../user-repository-picker';
import { LauncherDepsProvider } from '../../contexts/launcher-client-provider';

storiesOf('Pickers', module)
  .addDecorator((storyFn) => (
    <LauncherDepsProvider>
      {storyFn()}
    </LauncherDepsProvider>
  ))
  .add('UserRepositoryPicker', () => {
    return (
      <GitInfoLoader>
        {gitInfo => (
          <FormPanel
            initialValue={{}}
            validator={UserRepositoryPicker.checkCompletion}
            onSave={action('save')}
            onCancel={action('cancel')}
          >
            {(inputProps) => (<UserRepositoryPicker.Element {...inputProps} gitInfo={gitInfo}/>)}
          </FormPanel>
        )}
      </GitInfoLoader>
    );
  });
