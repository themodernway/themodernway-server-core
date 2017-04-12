/*
 * Copyright (c) 2017, The Modern Way. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.themodernway.server.core.security.tools

import java.awt.BorderLayout as BL
import java.awt.Color
import java.awt.Insets

import javax.swing.JFileChooser
import javax.swing.JMenuItem
import javax.swing.JTextArea
import javax.swing.border.CompoundBorder
import javax.swing.border.LineBorder
import javax.swing.border.MatteBorder
import javax.swing.filechooser.FileFilter

import com.themodernway.server.core.security.SimpleCryptoKeysGenerator

import groovy.swing.SwingBuilder as Swing

public class PropertiesUI {

    static SimpleCryptoKeysGenerator GENERATOR = new SimpleCryptoKeysGenerator()

    static Color BG_VALIDOK = rgb(0, 0, 0)

    static Color BG_INVALID = rgb(255, 9, 0)

    JMenuItem m_mast_save

    JFileChooser m_mast_file = new JFileChooser()

    public static void main(String...args) {
        new PropertiesUI(args)
    }

    private PropertiesUI(String...args) {
        JTextArea keys
        JTextArea text
        new Swing().edt {
            frame(title: 'Properties Encryption', size: [1200, 768], show: true) {
                borderLayout()
                def mbar = menuBar(constraints: BL.NORTH) {
                    menu('Application') {
                        menuItem('Open Master Keys file...', actionPerformed: {loadMasterKeys(fileChooser(), keys)})
                        m_mast_save = disableMenu(menuItem('Save Master Keys file...', actionPerformed: {ifMasterKeysValid(keys, {setupFileChooser(fileChooser(), {saveMasterKeys(it, keys)})})}))
                        menuItem('Set Prefix...', actionPerformed: {setupFileChooser(fileChooser(), {loadMasterKeys(it, keys)}).showOpenDialog(text)})
                        menuItem('Generate Keys', actionPerformed: {generateNewKeys(keys)})
                        menuItem('Exit', actionPerformed: {quit()})
                    }
                    menu('File') {
                        menuItem('Open...', actionPerformed: {setupFileChooser(fileChooser(), {loadProperties(it, text)}).showOpenDialog(text)})
                        menuItem('Save')
                        menuItem('Save As...', actionPerformed: {setupFileChooser(fileChooser(), {}).showOpenDialog(text)})
                        menuItem('Cancel')
                        menuItem('Revert')
                    }
                    menu('Encrypton') {
                        menuItem('Encrypt')
                        menuItem('Decrypt')
                    }
                }
                text = setupTextArea(textArea(text: '', rows: 800, columns: 120, constraints: BL.CENTER), new Insets(0, 0, 0, 0))
                keys = setupTextArea(textArea(text: '', rows: 2, columns: 120, constraints: BL.SOUTH), new Insets(1, 0, 0, 0))
            }
        }
    }

    public static Color rgb(int r, int g, int b) {
        new Color(toColorPercent(r), toColorPercent(g), toColorPercent(b))
    }

    public static float toColorPercent(int i) {
        (i/255.0f)
    }

    public JMenuItem enableMenu(JMenuItem menu) {
        menu.setEnabled(true)
        menu
    }

    public JMenuItem disableMenu(JMenuItem menu) {
        menu.setEnabled(false)
        menu
    }

    public void generateNewKeys(JTextArea t) {
        t.text = new StringBuilder().append("bootstrap.crypto.pass=").append(GENERATOR.getRandomPass()).append("\nbootstrap.crypto.salt=").append(GENERATOR.getRandomSalt()).toString()
        t.setForeground(BG_INVALID)
        m_mast_save.setEnabled(false)
        if (isPassValid(loadPropertiesFromString(t.text).getProperty('bootstrap.crypto.pass') as String)) {
            t.setForeground(BG_VALIDOK)
            m_mast_save.setEnabled(true)
        }
    }

    public void loadMasterKeys(JFileChooser f, JTextArea t) {
        t.text = ""
        t.setForeground(BG_INVALID)
        m_mast_save.setEnabled(false)
        setupFileChooser(f, {
            if(JFileChooser.APPROVE_OPTION == f.showOpenDialog(t)) {
                def s = f.getSelectedFile()
                if (s) {
                    t.text = s.text.trim()
                }
                if (isPassValid(loadPropertiesFromString(t.text).getProperty('bootstrap.crypto.pass') as String)) {
                    t.setForeground(BG_VALIDOK)
                    m_mast_save.setEnabled(true)
                }
            }
        })
    }

    public void saveMasterKeys(JFileChooser c, JTextArea t) {
        c.showSaveDialog(t)
        def f = c.getSelectedFile()
        if (f) {
        }
    }

    public void loadProperties(JFileChooser f, JTextArea t) {
        def s = f.getSelectedFile()
        if (s) {
            t.text = s.text.trim()
        }
        t.setForeground(BG_VALIDOK)
    }

    public JTextArea setupTextArea(JTextArea t, Insets i) {
        t.setBorder(new CompoundBorder(new CompoundBorder(new MatteBorder(i, Color.DARK_GRAY), new LineBorder(Color.LIGHT_GRAY, 1)), new LineBorder(Color.WHITE, 4)))
        t.setLineWrap(false)
        t.setEditable(false)
        t.setBackground(rgb(224,255,255))
        t.setForeground(BG_INVALID)
        t
    }

    public JFileChooser setupFileChooser(JFileChooser f, Closure c) {
        f.setAcceptAllFileFilterUsed(false)
        f.setMultiSelectionEnabled(false)
        f.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES)
        f.addChoosableFileFilter(new PropertiesFileFilter())
        if (c) {
            f.addActionListener { c(f) }
        }
        f
    }

    public void ifMasterKeysValid(JTextArea t, Closure c) {
        if (c && isPassValid(loadPropertiesFromString(t.text).getProperty('bootstrap.crypto.pass') as String)) {
            c()
        }
    }

    private Properties loadPropertiesFromString(String s) {
        def properties = new Properties()
        if (s) {
            s = s.trim()
            if (false == s.isEmpty()) {
                properties.load(new StringReader(s))
            }
        }
        properties
    }

    private boolean isPassValid(String pass) {
        if (pass) {
            pass = pass.trim()
            if (false == pass.isEmpty()) {
                return GENERATOR.isPassValid(pass)
            }
        }
        false
    }

    private quit() {
        System.exit(0)
    }

    private static final class PropertiesFileFilter extends FileFilter {

        private boolean m_checkwrite

        PropertiesFileFilter() {
            this(false)
        }

        PropertiesFileFilter(boolean checkwrite) {
            m_checkwrite = checkwrite
        }

        @Override
        public boolean accept(File f) {
            if(f.exists() && f.canRead()) {
                if (f.isDirectory()) {
                    return true
                }
                if (f.isFile() && f.getName().endsWith('.properties')) {
                    if (m_checkwrite) {
                        return f.canWrite()
                    }
                    return true
                }
            }
            return false
        }

        @Override
        public String getDescription() {
            "Property files"
        }
    }
}
