package com.jonathas.eloi.todo.utils

import android.app.DatePickerDialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.jonathas.eloi.todo.R
import com.jonathas.eloi.todo.adapter.ToDoAdapter
import com.jonathas.eloi.todo.objetos.Todo
import kotlinx.android.synthetic.main.dialog_adicionar_todo.view.*
import org.jetbrains.anko.db.*
import java.text.SimpleDateFormat
import java.util.*


class Utils {

    companion object {

        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        fun salvarTodo(database: SQLiteDatabase, todo: Todo)
        {
            database.insert("Todo",
                    "texto" to todo.texto,
                    "prioridade" to todo.prioridade,
                    "data" to todo.data,
                    "feito" to todo.feito
            )
        }

        fun buscarTodos(database: SQLiteDatabase, date: Date): ArrayList<Todo>
        {
            var listTodos = ArrayList<Todo>()

            database.select("Todo", "id", "data", "feito", "texto", "prioridade")
                    .whereArgs("(data = {todoData})", "todoData" to sdf.format(date))
                    .parseList(object : MapRowParser<List<Todo>> {

                        override fun parseRow(columns : Map<String, Any?>) : ArrayList<Todo> {

                            val id = columns.getValue("id")
                            val texto = columns.getValue("texto")
                            val prioridade = columns.getValue("prioridade")
                            val data = columns.getValue("data")
                            val feito = columns.getValue("feito").toString().equals("1")
                            val todos = Todo(id = id.toString(), texto = texto.toString(), prioridade = prioridade.toString(), data = data.toString(), feito = feito)
                            listTodos.add(todos)

                            return listTodos
                        }
                    })

            return listTodos
        }

        fun criarTabela(database: SQLiteDatabase) {
            database.createTable("Todo", true,
                    "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                    "texto" to TEXT,
                    "prioridade" to TEXT,
                    "data" to TEXT,
                    "feito" to INTEGER)
        }

        fun updateFeito (database: SQLiteDatabase, todo: Todo) {
            database.update("Todo", "feito" to todo.feito)
                    .whereArgs("id = {todoId}", "todoId" to todo.id)
                    .exec()
        }

        fun deletarTodo(database: SQLiteDatabase, todo: Todo): Int {
            return database.delete("Todo", "id = {todoId}", "todoId" to todo.id)
        }

        fun adicionarTodo(database: SQLiteDatabase, layoutInflater: LayoutInflater, context: Context,
                          cal: Calendar, todosAdapter: ToDoAdapter?) {
            var todo : Todo = Todo()

            var alertDialog = AlertDialog.Builder(context)

            val view = layoutInflater.inflate(R.layout.dialog_adicionar_todo, null)

            val radio_group = view.findViewById<RadioGroup>(R.id.rgPrioridades)
            val textoTodo = view.findViewById<EditText>(R.id.etTexto)

            alertDialog.setView(view)
            alertDialog.setTitle("ToDo") // O Titulo da notificação

            radio_group.setOnCheckedChangeListener(
                    { group, checkedId ->
                        val radio: RadioButton = view.findViewById(checkedId)
                        Toast.makeText(context," On checked change : ${radio.text}",
                                Toast.LENGTH_SHORT).show()

                        todo.prioridade = radio.text as String
                    })

            view.tvDataPicker.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                        DatePickerDialog(context,
                                DatePickerDialog.OnDateSetListener { view2, year, month, dayOfMonth ->
                                    cal.set(Calendar.YEAR, year)
                                    cal.set(Calendar.MONTH, month)
                                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                    view.tvDataPicker!!.text = sdf.format(cal.getTime())
                                },
                                // set DatePickerDialog to point to today's date when it loads up
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)).show()
                    }

            })

            alertDialog.setPositiveButton("Adicionar", { _, _ ->

                todo.texto = textoTodo.text.toString()
                if(!view.tvDataPicker!!.text.equals("--/--/----")) {
//                val date = SimpleDateFormat("dd/MM/yyyy").parse()
                    todo.data = view.tvDataPicker!!.text.toString()
                }
                todo.feito = false

                Utils.salvarTodo(database, todo)

                carregarTodos(database, todosAdapter)
            })

            alertDialog.setNegativeButton("Cancelar", { _, _ ->

            })
            alertDialog.show()
        }

        fun carregarTodos(database: SQLiteDatabase, todosAdapter: ToDoAdapter?) {
            var listTodos: ArrayList<Todo> = Utils.buscarTodos(database, Date())
            todosAdapter!!.setTodos(listTodos)
        }


        fun buscarTodosIncompletos(database: SQLiteDatabase, date: Date): ArrayList<Todo>
        {
            var listTodos = ArrayList<Todo>()

            database.select("Todo", "id", "data", "feito", "texto", "prioridade")
                    .whereArgs("(data = {todoData}) and (feito = {feitoo})", "todoData" to sdf.format(date), "feitoo" to false)
                    .parseList(object : MapRowParser<List<Todo>> {

                        override fun parseRow(columns : Map<String, Any?>) : ArrayList<Todo> {

                            val id = columns.getValue("id")
                            val texto = columns.getValue("texto")
                            val prioridade = columns.getValue("prioridade")
                            val data = columns.getValue("data")
                            val feito = columns.getValue("feito").toString().equals("1")
                            val todos = Todo(id = id.toString(), texto = texto.toString(), prioridade = prioridade.toString(), data = data.toString(), feito = feito)
                            listTodos.add(todos)

                            return listTodos
                        }
                    })

            return listTodos
        }
    }
}