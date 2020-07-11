package com.androidshowtime.notes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.androidshowtime.notes.databinding.FragmentNoteEditorBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_note_editor.*


class NoteEditorFragment : Fragment() {

    private lateinit var viewModel: NotesViewModel

    val args: NoteEditorFragmentArgs by navArgs()
    private var position = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View? {

        val binding = FragmentNoteEditorBinding.inflate(inflater)
        //get position argument from SafeArgs
        position = args.position
        viewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        // setting the fragment as the lifecycle owner
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        //set editText to blank string if this is a new note
        if (position == -1) {
            binding.detailEditText.text =
                    Editable.Factory.getInstance()
                            .newEditable("")
        }
        else {
            //if it is an old note put the note's existing text
            binding.detailEditText.text =
                    Editable.Factory.getInstance()
                            .newEditable(OverviewFragment.notesList[position])

        }




        viewModel.isListEdited.observe(viewLifecycleOwner, Observer {

            if (it == true) {
                readEditText()

                viewModel.listEditingDone()

            }
        })





        binding.detailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                //showing the save button
                viewModel.resetSaveButton()
                binding.button.visibility = View.VISIBLE
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })


        viewModel.isButtonPressed.observe(viewLifecycleOwner, Observer {

            if (it == true) {


                findNavController().navigate(
                    NoteEditorFragmentDirections.actionDetailNoteFragmentToOverviewFragment())
            }
        })
        //set the name of the Fragment Headline
        (activity as AppCompatActivity).supportActionBar?.title = "Edit Note"
        return binding.root
    }

    //saving on
    private fun readEditText() {

        //get text on the Edittext
        val editedNote = detailEditText.text.toString()

        //check if this is add new note request
        if (position == -1) {


            OverviewFragment.notesList.add(OverviewFragment.notesList.size, editedNote)


            val jsonList = Gson().toJson(OverviewFragment.notesList)
            OverviewFragment.sharedPrefs.edit()
                    .putString("list", jsonList)
                    .apply()

        }
        else {


            //code for editing note if note new
            OverviewFragment.notesList[position] = editedNote

            saveList(OverviewFragment.notesList)

        }


    }

    companion object {

        fun saveList(list: MutableList<String>) {
            val jsonList = Gson().toJson(list)
            OverviewFragment.sharedPrefs.edit()
                    .putString("list", jsonList)
                    .apply()

        }
    }


}
