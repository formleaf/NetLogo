// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app.common

import javax.swing.Action

trait MenuTab {
  val activeMenuActions = Seq.empty[Action]

  val permanentMenuActions: Seq[Action] = Seq.empty[Action]
}
