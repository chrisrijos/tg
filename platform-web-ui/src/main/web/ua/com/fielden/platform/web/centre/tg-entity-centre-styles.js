import '/resources/polymer/@polymer/polymer/polymer-legacy.js';
import '/resources/polymer/@polymer/polymer/lib/elements/dom-module.js';
import '/resources/polymer/@polymer/iron-flex-layout/iron-flex-layout.js';

const styleElement = document.createElement('dom-module');
styleElement.innerHTML = `
    <template>
        <style>
            .left-insertion-point, .right-insertion-point {
                @apply --layout-vertical;
                @apply --layout-start;
            }
            paper-icon-button.revers {
                transform: scale(-1, 1);
            }
            .selection-criteria {
                background-color: white;
            }
        </style>
    </template>
`;
styleElement.register('tg-entity-centre-styles');