const inquirer = require('inquirer');

module.exports = {
  askCredentials: () => {
    const questions = [
      {
        name: 'name',
        type: 'input',
        message: 'Please enter your name:',
        validate: function( value ) {
          if (value.length) {
            return true;
          } else {
            return 'Please enter your name:';
          }
        }
      }
    ];
    return inquirer.prompt(questions);
  },
};
