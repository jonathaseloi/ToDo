package com.jonathas.eloi.todo

import android.app.DatePickerDialog
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.EventLog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.facebook.stetho.Stetho
import com.imanoweb.calendarview.CustomCalendarView
import com.jonathas.eloi.todo.adapter.ToDoAdapter
import com.jonathas.eloi.todo.bd.database
import com.jonathas.eloi.todo.objetos.Todo
import com.jonathas.eloi.todo.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_adicionar_todo.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.jonathas.eloi.todo.R.id.calendarView
import android.widget.Toast
import com.imanoweb.calendarview.CalendarListener
import com.imanoweb.calendarview.DayDecorator
import com.jonathas.eloi.todo.R.id.calendarView
import android.graphics.Color.parseColor
import com.imanoweb.calendarview.DayView








class MainActivity : AppCompatActivity() , ToDoAdapter.Changer{

    var cal = Calendar.getInstance()
    var todosAdapter: ToDoAdapter? = null
    var listTodos : ArrayList<Todo> = ArrayList<Todo>()
    private val onItemClickListener: ToDoAdapter.Changer = this
    val myFormat = "dd/MM/yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Stetho.initializeWithDefaults(this)

        // Get the support action bar
        //val actionBar = supportActionBar

        Utils.criarTabela(database.writableDatabase)

        listTodos = Utils.buscarTodos(database.readableDatabase, Date())

        recyclerView()

        fabButton()
    }

    fun fabButton() {
        fab.setOnClickListener {
            Utils.adicionarTodo(
                    database.writableDatabase, layoutInflater,
                    this,
                    cal,
                    todosAdapter
                    )
        }
    }

    fun recyclerView() {
        todosAdapter = ToDoAdapter(this, onItemClickListener )

        rvDia.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvDia.adapter = todosAdapter

        todosAdapter!!.setTodos(listTodos)

        setTitle(Date())

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_filtro -> {

//                DatePickerDialog(this@MainActivity,
//                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
//                            cal.set(Calendar.YEAR, year)
//                            cal.set(Calendar.MONTH, month)
//                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//                            listTodos = Utils.buscarTodos(database.readableDatabase, cal.getTime())
//                            carregarTodos(cal.getTime())
//                        },
//                        // set DatePickerDialog to point to today's date when it loads up
//                        cal.get(Calendar.YEAR),
//                        cal.get(Calendar.MONTH),
//                        cal.get(Calendar.DAY_OF_MONTH)).show()

                abrirCalendario()

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun abrirCalendario() {
        var date: Date = Date()

        var alertDialog = AlertDialog.Builder(this)

        val view = layoutInflater.inflate(R.layout.dialog_calendario, null)

        val calendarioView = view.findViewById<CustomCalendarView>(R.id.calendarView)

        //Initialize calendar with date
        val currentCalendar = Calendar.getInstance(Locale.getDefault())

        //Show Monday as first date of week
        calendarioView.setFirstDayOfWeek(Calendar.MONDAY)

        //Show/hide overflow days of a month
        calendarioView.setShowOverflowDate(false)

        //call refreshCalendar to update calendar the view
        calendarioView.refreshCalendar(currentCalendar)

        //Handling custom calendar events
        calendarioView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date2: Date) {
                date = date2
                val df = SimpleDateFormat("dd-MM-yyyy")
                Toast.makeText(this@MainActivity, df.format(date), Toast.LENGTH_SHORT).show()
            }

            override fun onMonthChanged(date: Date) {
                val df = SimpleDateFormat("MM-yyyy")
                Toast.makeText(this@MainActivity, df.format(date), Toast.LENGTH_SHORT).show()
            }
        })

        //adding calendar day decorators
        var decorators: ArrayList<DayDecorator> = ArrayList<DayDecorator>()

        decorators.add(DisabledColorDecorator())
        calendarioView.setDecorators(decorators)
        calendarioView.refreshCalendar(currentCalendar)

        alertDialog.setView(view)
        alertDialog.setTitle("") // O Titulo da notificação

        alertDialog.setPositiveButton("Selecionar", { _, _ ->
            carregarTodos(date)
        })

        alertDialog.setNegativeButton("Cancelar", { _, _ ->

        })
        alertDialog.show()
    }

    private inner class DisabledColorDecorator : DayDecorator {
        override fun decorate(dayView: DayView) {
            if (Utils.buscarTodosIncompletos(database.readableDatabase, dayView.date).size > 0) {
                val color = Color.parseColor("#84ffff")
                dayView.setBackgroundColor(color)
            }
        }
    }

    override fun setFeito(todo: Todo) {
        Utils.updateFeito(database.writableDatabase, todo)
    }

    override fun deleteTodo(todo: Todo, position: Int) {
        val deletado = Utils.deletarTodo(database.writableDatabase, todo)

        if (deletado == 1) {
            todosAdapter!!.deletarTodo(position)
        }
    }

    fun carregarTodos(date: Date) {
        var listTodos: java.util.ArrayList<Todo> = Utils.buscarTodos(database.writableDatabase, date)
        todosAdapter!!.setTodos(listTodos)

        setTitle(date)
    }

    fun setTitle(date: Date) {
        val actionBar = supportActionBar
        actionBar?.title = "ToDo - " + sdf.format(date)
    }

}
