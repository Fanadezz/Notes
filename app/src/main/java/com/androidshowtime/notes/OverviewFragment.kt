package com.androidshowtime.notes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.androidshowtime.notes.databinding.FragmentOverviewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber


class OverviewFragment : Fragment() {
    companion object {
        //static components declared here
        lateinit var notesList: MutableList<String>
        lateinit var sharedPrefs: SharedPreferences
        lateinit var arrayAdapter: ArrayAdapter<String>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //call this method inorder to show menu on a fragment
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {


        /* initializing sharedPreferences - requireActivity() a method
       that returns the non-null activity instance to fragment or throws an exception.*/

        sharedPrefs = requireActivity().getSharedPreferences(
            getString(R.string.shared_prefs_file),
            Context.MODE_PRIVATE
                                                            )
        // Inflate the layout for this fragment
        val binding = FragmentOverviewBinding.inflate(inflater)


        //initializing the viewModel
        val viewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        // setting the fragment as the lifecycle owner
        binding.lifecycleOwner = this



        binding.viewModel = viewModel

        viewModel.listEditingDone()

        notesList = retrieveNotesList()
        Timber.i("is the notesListEmpty:- ${notesList.isEmpty()}")

        if (notesList.isEmpty()) {

            notesList.add("Empty Note - Click to Edit")
        }


        arrayAdapter =
                ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, notesList)

        binding.listView.adapter = arrayAdapter

        binding.listView.setOnItemClickListener { adapterView, view, i, l ->
            findNavController().navigate(
                OverviewFragmentDirections.actionOverviewFragmentToDetailNoteFragment(
                    i
                                                                                     )
                                        )

        }

        binding.listView.setOnItemLongClickListener { adapterView, view, i, l ->

            val materialBuilder =
                    MaterialAlertDialogBuilder(requireActivity()).setIcon(android.R.drawable
                                                                                  .ic_delete)
                            .setTitle("Delete Entry")
                            .setMessage("Do you want to delete entry")
                            //use x instead of i to avoid name shadowing
                            .setPositiveButton("Delete") { _, _ ->
                                notesList.removeAt(i)
                                arrayAdapter.notifyDataSetChanged()
                                NoteEditorFragment.saveList(notesList)
                            }
                            .setNegativeButton("Cancel", null)
                            .show()




            true
        }



        (activity as AppCompatActivity).supportActionBar?.title = "Notes List"


        /*hiding the keyboard so that it doesn't overlap the listView when entering the this
        fragment from NotesEditorFragment*/
        binding.listView.hideKeyboard()
        return binding.root
    }

    private fun retrieveNotesList(): MutableList<String> {

        //declare List Variable
        val list: MutableList<String>
        //getting the saved list from json

        val jsonString = sharedPrefs.getString("list", "")

        list = when {

            //if null or empty return an empty MutableList
            jsonString.isNullOrEmpty() -> {
                mutableListOf()
            }
            //otherwise return the saved list
            else -> Gson().fromJson(jsonString, object : TypeToken<List<String>>() {}.type)
        }

        return list
    }

    //creating menu on the fragment
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when (item.itemId) {

            R.id.add_new_note -> {

                /* in case of newNote identify this action with -1 and pass
                 it using Navigation SafeArgs*/
                findNavController().navigate(OverviewFragmentDirections
                                                     .actionOverviewFragmentToDetailNoteFragment(
                                                         -1))
                true
            }

            R.id.delete_all_notes -> {
                notesList.clear()

                //call save method from NoteEditorFragment
                NoteEditorFragment.saveList(notesList)

                arrayAdapter.notifyDataSetChanged()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //method to hide keyboard
    private fun View.hideKeyboard() {
        val inputManager =
                requireActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}
