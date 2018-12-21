/**
 * DO NOT EDIT
 *
 * This file was automatically generated by
 *   https://github.com/Polymer/tools/tree/master/packages/gen-typescript-declarations
 *
 * To modify these typings, edit the source file(s):
 *   paper-tab.js
 */

import {IronButtonState} from '@polymer/iron-behaviors/iron-button-state.js';

import {IronControlState} from '@polymer/iron-behaviors/iron-control-state.js';

import {PaperRippleBehavior} from '@polymer/paper-behaviors/paper-ripple-behavior.js';

import {Polymer} from '@polymer/polymer/lib/legacy/polymer-fn.js';

import {dom} from '@polymer/polymer/lib/legacy/polymer.dom.js';

import {html} from '@polymer/polymer/lib/utils/html-tag.js';

import {LegacyElementMixin} from '@polymer/polymer/lib/legacy/legacy-element-mixin.js';

/**
 * `paper-tab` is styled to look like a tab. It should be used in conjunction with
 * `paper-tabs`.
 *
 * Example:
 *
 *     <paper-tabs selected="0">
 *       <paper-tab>TAB 1</paper-tab>
 *       <paper-tab>TAB 2</paper-tab>
 *       <paper-tab>TAB 3</paper-tab>
 *     </paper-tabs>
 *
 * ### Styling
 *
 * The following custom properties and mixins are available for styling:
 *
 * Custom property | Description | Default
 * ----------------|-------------|----------
 * `--paper-tab-ink` | Ink color | `--paper-yellow-a100`
 * `--paper-tab` | Mixin applied to the tab | `{}`
 * `--paper-tab-content` | Mixin applied to the tab content | `{}`
 * `--paper-tab-content-unselected` | Mixin applied to the tab content when the tab is not selected | `{}`
 *
 * This element applies the mixin `--paper-font-common-base` but does not import
 * `paper-styles/typography.html`. In order to apply the `Roboto` font to this
 * element, make sure you've imported `paper-styles/typography.html`.
 */
interface PaperTabElement extends IronControlState, IronButtonState, PaperRippleBehavior, LegacyElementMixin, HTMLElement {

  /**
   * If true, the tab will forward keyboard clicks (enter/space) to
   * the first anchor element found in its descendants
   */
  link: boolean|null|undefined;
  readonly _parentNoink: any;
  attached(): void;
  _updateNoink(): void;
  _onTap(event: any): void;
}

export {PaperTabElement};

declare global {

  interface HTMLElementTagNameMap {
    "paper-tab": PaperTabElement;
  }
}
