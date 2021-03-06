// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.swing

import java.awt._
import java.awt.event._
import javax.swing._
import javax.swing.event._
import scala.language.implicitConversions

object Implicits {
  implicit def thunk2action[T](fn: () => T): Action = new AbstractAction {
    override def actionPerformed(e: ActionEvent) = fn()
  }
  implicit def thunk2windowAdapter[T](fn: () => T) = new WindowAdapter {
    override def windowClosing(e: WindowEvent) = fn()
  }
  implicit def JComboBoxToIterable[T](jcb: JComboBox[T]) =
    (0 until jcb.getItemCount).map(jcb.getItemAt)
  implicit def thunk2documentListener[T](fn: () => T): DocumentListener = thunk2documentListener(_ => fn())
  implicit def thunk2documentListener[T](fn: DocumentEvent => T) = new DocumentListener {
    def changedUpdate(e: DocumentEvent) = fn(e)
    def insertUpdate(e: DocumentEvent)  = fn(e)
    def removeUpdate(e: DocumentEvent)  = fn(e)
  }
  implicit def thunk2keyListener[T](fn: () => T): KeyListener = thunk2keyListener(_ => fn())
  implicit def thunk2keyListener[T](fn: KeyEvent => T) = new KeyListener {
    def keyReleased(e: KeyEvent) = fn(e)
    def keyTyped(e: KeyEvent)    = fn(e)
    def keyPressed(e: KeyEvent)  = fn(e)
  }

  implicit def EnrichComboBox[T](combo: JComboBox[T]) = RichJComboBox[T](combo)
  implicit def EnrichContainer(c: Container) = new RichComponent(c)
}

object RichJButton{
  def apply(name:String)(f: => Unit) = {
    new JButton(name){
      addActionListener(new ActionListener{
        def actionPerformed(e:ActionEvent) { f }
      })
    }
  }
  def apply(icon:ImageIcon)(f: => Unit) = {
    new JButton(icon){
      addActionListener(new ActionListener{
        def actionPerformed(e:ActionEvent) { f }
      })
    }
  }
  def apply(action:AbstractAction)(f: => Unit) = {
    new JButton(action){
      addActionListener(new ActionListener{
        def actionPerformed(e:ActionEvent) { f }
      })
    }
  }
}

class RichComponent(c:Container){
  def addAll(comps:Component*){
    for(c2<-comps) c.add(c2)
  }
}

object RichAction{
  def apply(name:String)(f: ActionEvent => Unit): AbstractAction = new AbstractAction(name){
    def actionPerformed(e: ActionEvent) { f(e) }
  }
  def apply(f: ActionEvent => Unit): AbstractAction = new AbstractAction(){
    def actionPerformed(e: ActionEvent) { f(e) }
  }
}

// open question
// can we use structural typing to add this method to anything with an addActionListener method?
object RichJMenuItem {
  def apply(name:String)(f: => Unit) = {
    new JMenuItem(name){
      addActionListener(new ActionListener{
        def actionPerformed(e:ActionEvent) { f }
      })
    }
  }
}

case class RichJComboBox[T](combo: JComboBox[T]) {
  class PossibleSelection(t: T) {
    def becomesSelected(f: => Unit) {
      combo.addItemListener(new ItemListener {
        def itemStateChanged(e: ItemEvent) {
          if (e.getItem == t && e.getStateChange == ItemEvent.SELECTED) { f }
        }
      })
    }
  }
  def when(t: T) = new PossibleSelection(t)
  def containsItem(t:T) = {
    (0 until combo.getModel.getSize).toList.exists((i:Int) => { combo.getItemAt(i) == t })
  }
}
