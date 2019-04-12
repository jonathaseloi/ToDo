package com.jonathas.eloi.todo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonathas.eloi.todo.R
import com.jonathas.eloi.todo.objetos.Todo
import kotlinx.android.synthetic.main.rv_item.view.*

class ToDoAdapter(private val context: Context,
                  val clickChanger: Changer) : RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {


    private var todos: ArrayList<Todo>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false)
        return ViewHolder(view)
    }

    fun setTodos(todos: ArrayList<Todo>) {
        this.todos = todos
        notifyDataSetChanged()
    }

    fun deletarTodo(position: Int) {
        todos!!.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = todos!![position]

        holder.let {
            holder.cbTodo.isChecked = todo.feito
            holder.tvTexto.text = todo.texto

            if (todo.prioridade.equals("Amarelo")) {
                holder.ivPrioridade.setBackgroundResource(R.color.amarelo)
            } else if (todo.prioridade.equals("Vermelho")) {
                holder.ivPrioridade.setBackgroundResource(R.color.vermelho)
            } else if (todo.prioridade.equals("Verde")) {
                holder.ivPrioridade.setBackgroundResource(R.color.verde)
            }

            holder.cbTodo!!.setOnCheckedChangeListener { _, isChecked ->
                todo.feito = isChecked
                clickChanger.setFeito(todo)
            }

            holder.ivDelete!!.setOnClickListener {
                clickChanger.deleteTodo(todo, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return todos!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPrioridade = itemView.ivPrioridade
        val ivDelete = itemView.ivDelete
        val cbTodo = itemView.cbTodo
        val tvTexto = itemView.tvTexto
    }

    interface Changer {
        fun setFeito(todo: Todo)
        fun deleteTodo(todo: Todo, position: Int)
    }

}