/**
 * @license
 * Copyright (c) 2018 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt The complete set of authors may be found
 * at http://polymer.github.io/AUTHORS.txt The complete set of contributors may
 * be found at http://polymer.github.io/CONTRIBUTORS.txt Code distributed by
 * Google as part of the polymer project is also subject to an additional IP
 * rights grant found at http://polymer.github.io/PATENTS.txt
 */
/// <reference types="socket.io" />
/**
 * A socket for communication between the CLI and browser runners.
 *
 * @param {string} browserId An ID generated by the CLI runner.
 * @param {!io.Socket} socket The socket.io `Socket` to communicate over.
 */
export default class CLISocket {
    private readonly socket;
    private readonly browserId;
    constructor(browserId: string, socket: SocketIO.Socket);
    /**
     * @param {!Mocha.Runner} runner The Mocha `Runner` to observe, reporting
     *     interesting events back to the CLI runner.
     */
    observe(runner: Mocha.IRunner): void;
    /**
     * @param {string} event The name of the event to fire.
     * @param {*} data Additional data to pass with the event.
     */
    emitEvent(event: string, data?: {}): void;
    /**
     * Builds a `CLISocket` if we are within a CLI-run environment; short-circuits
     * otherwise.
     *
     * @param {function(*, CLISocket)} done Node-style callback.
     */
    static init(done: (error?: {} | null | undefined, socket?: CLISocket) => void): void;
}
